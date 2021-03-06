package com.pss.project.service;

import com.pss.project.model.Delegation;
import com.pss.project.model.User;
import com.pss.project.repository.DelegationRepository;
import com.pss.project.repository.UserRepository;
import com.pss.project.util.Transport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DelegationService {

    private UserRepository userRepository;
    private DelegationRepository delegationRepository;

    @Autowired
    public DelegationService(UserRepository userRepository, DelegationRepository delegationRepository) {
        this.userRepository = userRepository;
        this.delegationRepository = delegationRepository;
    }

    public List<Delegation> getAllDelegations() {
        return delegationRepository.findAll();
    }

    public List<Delegation> getAllDelegationsOrderByDateTimeStartDesc() {
        return delegationRepository.findByOrderByDateTimeStartDesc();
    }

    public List<Delegation> getAllDelByUserByDateTimeStartDesc(String email) {
        return delegationRepository.findAllByUserEmailOrderByDateTimeStartDesc(email);
    }

    public List<Delegation> getAllFutAndNotConfirmedDelByUserByDateTimeStartDesc(String email) {
        LocalDateTime localDateTime = LocalDateTime.now();

        return delegationRepository.findAllByUserEmailOrderByDateTimeStartDesc(email)
                .stream()
                .filter(del -> del.getDateTimeStop().isAfter(localDateTime))
                .filter(del -> !del.isConfirmed())
                .collect(Collectors.toList());
    }

    public List<Delegation> getAllDelByUserByConfirmation(String email, boolean isConfirmed) {
        return delegationRepository.findAllByUserEmailAndConfirmed(email, isConfirmed);
    }

    public List<Delegation> getAllNotRequestedDelByUserByConfirmation(String email, boolean isConfirmed) {
        return delegationRepository.findAllByUserEmailAndRequestStatus(email, null)
                .stream()
                .filter(del -> del.isConfirmed() == isConfirmed)
                .collect(Collectors.toList());
    }

    public List<Delegation> getAllDelByRequestStatus(Boolean status) {
        return delegationRepository.findAllByRequestStatus(status);
    }

    public ResponseEntity<Delegation> requestByStatus(Long delegationId, Boolean status) {
        Optional<Delegation> del = delegationRepository.findById(delegationId);

        if(del.isPresent()) {
            Delegation delegation = del.get();
            try {
                delegation.setRequestStatus(status);
                delegation = delegationRepository.save(delegation);
            }
            catch(Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(delegation, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Delegation> answerRequestById(Long delegationId) {
        Optional<Delegation> del = delegationRepository.findById(delegationId);

        if(del.isPresent()) {
            Delegation delegation = del.get();
            try {
                Boolean status = delegation.getRequestStatus();
                delegation.setConfirmed(status);
                delegation.setRequestStatus(null);

                delegation = delegationRepository.save(delegation);
            }
            catch(Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(delegation, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Delegation> addDelegation(String email, Delegation delegation) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent()) {
            if (delegation.getDateTimeStop().isAfter(delegation.getDateTimeStart())) {
                try {
                    if(delegation.getTransport().equals(Transport.CAR)) {
                        delegation.setTicketPrice(null);
                    }
                    else {
                        delegation.setAutoCapacity(null);
                        delegation.setDistance(null);
                    }
                    delegation.setUser(user.get());
                    delegation.setConfirmed(false);
                    delegation.setRequestStatus(null);

                    delegation = delegationRepository.save(delegation);
                }
                catch (Exception e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(delegation, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Delegation> changeDelegation(Long delegationId, Delegation del) {
        Optional<Delegation> thisDelegation = delegationRepository.findById(delegationId);

        if(thisDelegation.isPresent()) {
            if (del.getDateTimeStop().isAfter(del.getDateTimeStart())) {
                Delegation oldDel = thisDelegation.get();
                Delegation newDel;
                try {
                    newDel = new Delegation(oldDel.getId(), del.getDescription(), oldDel.getUser(),
                            del.getDateTimeStart(), del.getDateTimeStop(),
                            del.getTravelDietAmount(), del.getBreakfastNumber(),
                            del.getDinnerNumber(), del.getSupperNumber(), del.getTransport(),
                            del.getTransport().equals(Transport.CAR) ? null : del.getTicketPrice(),
                            !del.getTransport().equals(Transport.CAR) ? null : del.getAutoCapacity(),
                            !del.getTransport().equals(Transport.CAR) ? null : del.getDistance(),
                            del.getAccommodationPrice(), del.getOtherTicketsPrice(),
                            del.getOtherOutlayDesc(), del.getOtherOutlayPrice(),
                            oldDel.isConfirmed(), oldDel.getRequestStatus());

                    newDel = delegationRepository.save(newDel);
                }
                catch (Exception e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(newDel, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Boolean> removeDelegation(Long delegationId) {
        Optional<Delegation> delegation = delegationRepository.findById(delegationId);

        if(delegation.isPresent()) {
            Delegation thisDelegation = delegation.get();
            try {
                delegationRepository.delete(thisDelegation);
            }
            catch(Exception e) {
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}