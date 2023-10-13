package mk.ukim.finki.wp.june2022.g1.service.impl;

import mk.ukim.finki.wp.june2022.g1.model.OSType;
import mk.ukim.finki.wp.june2022.g1.model.User;
import mk.ukim.finki.wp.june2022.g1.model.VirtualServer;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidVirtualMachineIdException;
import mk.ukim.finki.wp.june2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.june2022.g1.repository.VirtualServerRepository;
import mk.ukim.finki.wp.june2022.g1.service.VirtualServerService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class VirtualServerServiceImpl implements VirtualServerService {

    private final VirtualServerRepository virtualServerRepository;
    private final UserRepository userRepository;

    public VirtualServerServiceImpl(VirtualServerRepository virtualServerRepository, UserRepository userRepository) {
        this.virtualServerRepository = virtualServerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<VirtualServer> listAll() {
        return virtualServerRepository.findAll();
    }

    @Override
    public VirtualServer findById(Long id) {
        return virtualServerRepository.findById(id)
                .orElseThrow(InvalidVirtualMachineIdException::new);
    }

    @Override
    public VirtualServer create(String name, String ipAddress, OSType osType, List<Long> owners, LocalDate launchDate) {
        List<User> ownersList = userRepository.findAllById(owners);
        if(ownersList.isEmpty()){
            throw new InvalidUserIdException();
        }
        VirtualServer virtualServer= new VirtualServer(name, ipAddress, osType, ownersList, launchDate);
        virtualServerRepository.save(virtualServer);
        return virtualServer;
    }

    @Override
    public VirtualServer update(Long id, String name, String ipAddress, OSType osType, List<Long> owners) {
        VirtualServer virtualServer = virtualServerRepository.findById(id)
                .orElseThrow(InvalidVirtualMachineIdException::new);
        List<User> ownersList = userRepository.findAllById(owners);
        if(ownersList.isEmpty()){
            throw new InvalidUserIdException();
        }
        virtualServer.setInstanceName(name);
        virtualServer.setIpAddress(ipAddress);
        virtualServer.setOSType(osType);
        virtualServer.setOwners(ownersList);
        virtualServerRepository.save(virtualServer);
        return virtualServer;
    }

    @Override
    public VirtualServer delete(Long id) {
        VirtualServer virtualServer = virtualServerRepository.findById(id)
                .orElseThrow(InvalidVirtualMachineIdException::new);
        virtualServerRepository.delete(virtualServer);
        return virtualServer;
    }

    @Override
    public VirtualServer markTerminated(Long id) {
        VirtualServer virtualServer = virtualServerRepository.findById(id)
                .orElseThrow(InvalidVirtualMachineIdException::new);
        virtualServer.setTerminated(true);
        virtualServerRepository.save(virtualServer);
        return virtualServer;
    }

    @Override
    public List<VirtualServer> filter(Long ownerId, Integer activeMoreThanDays) {
        if(ownerId!=null && activeMoreThanDays!=null){
            User user = userRepository.findById(ownerId)
                    .orElseThrow(InvalidUserIdException::new);
            return virtualServerRepository
                    .findAllByOwnersContainsAndLaunchDateBefore(user,
                            LocalDate.now().minusDays(activeMoreThanDays));
        }
        else if (ownerId!=null) {
            User user = this.userRepository.findById(ownerId)
                    .orElseThrow(InvalidUserIdException::new);
            return virtualServerRepository.findAllByOwnersContains(user);
        }
        else if (activeMoreThanDays!=null) {
            return virtualServerRepository.findAllByLaunchDateBefore(LocalDate
                    .now().minusDays(activeMoreThanDays));
        }
        else
            return virtualServerRepository.findAll();
    }
}
