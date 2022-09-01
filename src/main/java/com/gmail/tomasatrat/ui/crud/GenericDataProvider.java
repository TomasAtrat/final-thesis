package com.gmail.tomasatrat.ui.crud;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GenericDataProvider<T> extends AbstractBackEndDataProvider<T, CrudFilter>{
    final ICrudService service;

    private Consumer<Long> sizeChangeListener;

    public GenericDataProvider(ICrudService service) {
        this.service = service;
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream stream = service.findAll().stream();

        if (query.getFilter().isPresent()) {
            stream = stream
                    .filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<T, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    private Predicate<T> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<T>) entity -> {
                    try {
                        Object value = valueOf(constraint.getKey(), entity);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .reduce(Predicate::and)
                .orElse(e -> true);
    }

    private Comparator<T> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream()
                .map(sortClause -> {
                    try {
                        Comparator<T> comparator
                                = Comparator.comparing(entity ->
                                (Comparable) valueOf(sortClause.getKey(), entity));

                        if (sortClause.getValue() == SortDirection.DESCENDING) {
                            comparator = comparator.reversed();
                        }

                        return comparator;
                    } catch (Exception ex) {
                        return (Comparator<T>) (o1, o2) -> 0;
                    }
                })
                .reduce(Comparator::thenComparing)
                .orElse((o1, o2) -> 0);
    }

    private Object valueOf(String fieldName, T entity) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void persist(IDataEntity item) {
        service.addItem(item);
    }

    Optional<T> findByID(Long id) {
        return service.findByID(id);
    }

    void delete(IDataEntity item) {
        service.delete(item);
    }
}


// end-source-example
