package edu.hsai.movierecommendation.abstraction.serviceImpl;

import edu.hsai.movierecommendation.abstraction.service.UserService;
import edu.hsai.movierecommendation.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;

    @Override
    public UserDto getById(Long id) {
        return UserDto.fromDbEntity(userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No user with id %d", id))));
    }

    @Override
    public Long addUser(AddUserDto addUserDto) {
        return userRepo.save(AddUserDto.toDbEntity(addUserDto)).getId();
    }

    @Override
    public UserDto getByNickname(String nickname) {
        return UserDto.fromDbEntity(userRepo.findByNickname(nickname).orElseThrow(EntityNotFoundException::new));
    }
}
