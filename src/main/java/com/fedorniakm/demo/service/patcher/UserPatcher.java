package com.fedorniakm.demo.service.patcher;

import com.fedorniakm.demo.model.User;
import com.fedorniakm.demo.model.UserPatch;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserPatcher implements Patcher<User, UserPatch> {

    @Override
    public void patch(User target, UserPatch patch) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(patch);
        patchNotEmpty(patch.getEmail(), target::setEmail);
        patchNotEmpty(patch.getFirstName(), target::setFirstName);
        patchNotEmpty(patch.getLastName(), target::setLastName);
        patchNonNull(patch.getBirthDate(), target::setBirthDate);
        patchNonNull(patch.getAddress(), value -> target.setAddress(Optional.of(value)));
        patchNonNull(patch.getPhoneNumber(), value -> target.setPhoneNumber(Optional.of(value)));
    }
}
