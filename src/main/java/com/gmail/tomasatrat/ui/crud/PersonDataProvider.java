package com.gmail.tomasatrat.ui.crud;

import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.service.BranchService;
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

public class PersonDataProvider extends AbstractBackEndDataProvider<Branch, CrudFilter> {

        // A real app should hook up something like JPA
        final BranchService ser;

        private Consumer<Long> sizeChangeListener;

        public PersonDataProvider(BranchService branchService) {
            this.ser = branchService;
        }

        @Override
        protected Stream<Branch> fetchFromBackEnd(Query<Branch, CrudFilter> query) {
            int offset = query.getOffset();
            int limit = query.getLimit();

            Stream<Branch> stream = ser.findAll().stream();

            if (query.getFilter().isPresent()) {
                stream = stream
                        .filter(predicate(query.getFilter().get()))
                        .sorted(comparator(query.getFilter().get()));
            }

            return stream.skip(offset).limit(limit);
        }

        @Override
        protected int sizeInBackEnd(Query<Branch, CrudFilter> query) {
            // For RDBMS just execute a SELECT COUNT(*) ... WHERE query
            long count = fetchFromBackEnd(query).count();

            if (sizeChangeListener != null) {
                sizeChangeListener.accept(count);
            }

            return (int) count;
        }

        void setSizeChangeListener(Consumer<Long> listener) {
            sizeChangeListener = listener;
        }

        private static Predicate<Branch> predicate(CrudFilter filter) {
            // For RDBMS just generate a WHERE clause
            return filter.getConstraints().entrySet().stream()
                    .map(constraint -> (Predicate<Branch>) person -> {
                        try {
                            Object value = valueOf(constraint.getKey(), person);
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

        private static Comparator<Branch> comparator(CrudFilter filter) {
            // For RDBMS just generate an ORDER BY clause
            return filter.getSortOrders().entrySet().stream()
                    .map(sortClause -> {
                        try {
                            Comparator<Branch> comparator
                                    = Comparator.comparing(person ->
                                    (Comparable) valueOf(sortClause.getKey(), person));

                            if (sortClause.getValue() == SortDirection.DESCENDING) {
                                comparator = comparator.reversed();
                            }

                            return comparator;
                        } catch (Exception ex) {
                            return (Comparator<Branch>) (o1, o2) -> 0;
                        }
                    })
                    .reduce(Comparator::thenComparing)
                    .orElse((o1, o2) -> 0);
        }

        private static Object valueOf(String fieldName, Branch person) {
            try {
                Field field = Branch.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(person);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public void persist(Branch item) {
            ser.addItem(item);
        }

        Optional<Branch> findByID(Long id) {
            return ser.findByID(id);
        }

        void delete(Branch item) {
            ser.delete(item);
        }
    }

    // end-source-example
