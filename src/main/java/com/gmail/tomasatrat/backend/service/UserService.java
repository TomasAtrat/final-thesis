package com.gmail.tomasatrat.backend.service;

import com.gmail.tomasatrat.app.security.SecurityUtils;
import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.Branch;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.data.entity.VProductivityByUser;
import com.gmail.tomasatrat.backend.microservices.branch.services.BranchService;
import com.gmail.tomasatrat.backend.data.entity.VProductivityByUser;
import com.gmail.tomasatrat.backend.repositories.VProductivityByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements FilterableCrudService<User>, ICrudService {

	public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "El usuario ha sido bloqueado y no puede ser modificado ni eliminado";
	private static final String DELETING_SELF_NOT_PERMITTED = "No puedes eliminar tu propia cuenta";
	private final UserRepository userRepository;
	private final VProductivityByUserRepository productivityByUserRepository;
	private BranchService branchService;

	@Autowired
	public UserService(UserRepository userRepository, BranchService branchService, VProductivityByUserRepository productivityByUserRepository) {
		this.userRepository = userRepository;
		this.productivityByUserRepository = productivityByUserRepository;
		this.branchService = branchService;
	}

	public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return getRepository()
					.findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
							repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter, pageable);
		} else {
			return find(pageable);
		}
	}

	@Override
	public long countAnyMatching(Optional<String> filter) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return userRepository.countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
					repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter);
		} else {
			return count();
		}
	}

	@Override
	public UserRepository getRepository() {
		return userRepository;
	}

	public Page<User> find(Pageable pageable) {
		return getRepository().findBy(pageable);
	}

	@Override
	public User save(User currentUser, User entity) {
		throwIfUserLocked(entity);
		return getRepository().saveAndFlush(entity);
	}

	@Override
	@Transactional
	public void delete(User currentUser, User userToDelete) {
		throwIfDeletingSelf(currentUser, userToDelete);
		throwIfUserLocked(userToDelete);
		FilterableCrudService.super.delete(currentUser, userToDelete);
	}

	private void throwIfDeletingSelf(User currentUser, User user) {
		if (currentUser.equals(user)) {
			throw new UserFriendlyDataException(DELETING_SELF_NOT_PERMITTED);
		}
	}

	private void throwIfUserLocked(User entity) {
		if (entity != null && entity.isLocked()) {
			throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
		}
	}

	@Override
	public User createNew(User currentUser) {
		return new User();
	}

	public User findByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}

	public void toggleStatus(User user, Boolean value) {
		user.setActive(value);
		this.userRepository.save(user);
	}

	@Override
	public List<User> findAll() {
		return this.userRepository.findAll();
	}

	public List<User> findAllByActiveIsTrue() {
		return this.userRepository.findAllByActiveIsTrue();
	}

	@Override
	public IDataEntity addItem(IDataEntity item) {
		User user = (User) item;
		user.setIdBranch(userRepository.findByUsername(SecurityUtils.getUsername()).getIdBranch());
		user.setActive(true);

		return this.userRepository.save((User) item);
	}

	@Override
	public Optional<User> findByID(Long id) {
		return this.userRepository.findById(id);
	}

	@Override
	public void delete(IDataEntity item) {
		this.userRepository.delete((User) item);
	}

	public Integer getProductivityInMinutes(Long userId) {
		List<VProductivityByUser> entity = this.productivityByUserRepository.findByUser(userId);
		long diff = 0;
		int totalMinutes = 0;
		for (int i = 0; i < entity.size(); i++) {
			diff = diff + (entity.get(i).getEndingDate().getTime() - entity.get(i).getStartingDate().getTime());
		}

		if (diff != 0) {
			long auxLong = TimeUnit.MILLISECONDS.toMinutes(diff);
			totalMinutes = Math.toIntExact(auxLong / entity.size());
		}
		return totalMinutes;
	}

	public Branch getUserBranchByUsername(String username){
		User user = findByUsername(username);
		return branchService.findByID(user.getIdBranch()).get();
	}
}
