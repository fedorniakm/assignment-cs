package com.fedorniakm.demo.service.patcher;

import com.fedorniakm.demo.model.UserPatch;
import com.fedorniakm.demo.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserEntityPatcher implements Patcher<UserEntity, UserPatch> {

    @Override
    public void patch(UserEntity target, UserPatch patch) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(patch);
        patchNotEmpty(patch.getEmail(), target::setEmail);
        patchNotEmpty(patch.getFirstName(), target::setFirstName);
        patchNotEmpty(patch.getLastName(), target::setLastName);
        patchNonNull(patch.getBirthDate(), target::setBirthDate);
        patchNonNull(patch.getAddress(), target::setAddress);
        patchNonNull(patch.getPhoneNumber(), target::setPhoneNumber);
    }
}
