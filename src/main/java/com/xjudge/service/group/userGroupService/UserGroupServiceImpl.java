package com.xjudge.service.group.userGroupService;

import com.xjudge.entity.Group;
import com.xjudge.entity.User;
import com.xjudge.entity.UserGroup;
import com.xjudge.exception.XJudgeException;
import com.xjudge.model.enums.UserGroupRole;
import com.xjudge.repository.UserGroupRepository;
import com.xjudge.repository.UserRepo;
import com.xjudge.service.group.GroupServiceImpl;
import com.xjudge.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final UserService userService;
    @Override
    public boolean existsByUserAndGroup(User user, Group group) {
        return userGroupRepository.existsByUserAndGroup(user, group);
    }

    @Override
    public UserGroup findByUserAndGroup(User user, Group group) {
        return userGroupRepository.findByUserAndGroup(user, group).orElseThrow(
                () -> new XJudgeException("User not found in group.", GroupServiceImpl.class.getName(), HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public UserGroup findByUserHandleAndGroupId(String userHandle, Long groupId) {
        return userGroupRepository.findByUserHandleAndGroupId(userHandle, groupId).orElseThrow(
                () -> new XJudgeException("User not found in group.", GroupServiceImpl.class.getName(), HttpStatus.NOT_FOUND)
        );
    }

    // TODO - handle this function to get groups where user has role LEADER or ADMIN
    @Override
    public List<UserGroup> findAllByUserAndRole(User user) {
        List<UserGroup> groups = userGroupRepository.findAllByUser(user);
        return groups.stream()
                .filter(group ->
                        group.getRole() == UserGroupRole.LEADER
                        || group.getRole() == UserGroupRole.ADMIN
                ).toList();
    }


    @Override
    public void save(UserGroup userGroup) {
        userGroupRepository.save(userGroup);
    }

    @Override
    public void delete(UserGroup userGroup) {
        userGroupRepository.delete(userGroup);
    }

    @Override
    public UserGroupRole findRoleByUserAndGroupId(Principal connectedUser, Long groupId)
    {
        User user=userService.findUserByHandle(connectedUser.getName());
        Long id=user.getId();
        return userGroupRepository.findByUserIdAndGroupId(id,groupId).getRole();
    }
}
