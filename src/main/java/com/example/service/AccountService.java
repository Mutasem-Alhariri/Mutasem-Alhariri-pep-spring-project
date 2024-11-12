package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.entity.Account;
import com.example.exception.NotUniqueUserException;
import com.example.exception.UnAuthorizedUserException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
    * @param account Tha account to be created
    * @return the account including the generated account_id if successful
    * @throws NotUniqueUserException if the username is already used
    */
    public Account createAccount(Account account) throws NotUniqueUserException{
        if(validateAccount(account)) {
            if(accountRepository.existsByUsername(account.getUsername())) {
                throw new NotUniqueUserException("Conflict");
            }
            return accountRepository.save(account);
        }
         return account;
    }

    /**
  * Authenticate the account with the given username and password
  * @param account the account to be authenticated
  * @return the account if it exists
  * @throws UnAuthorizedUserException if username or password are invalid
  */
    public Account login(Account account) throws UnAuthorizedUserException{
        Account authenticated = accountRepository.findByUsernameAndPassword(account.getUsername(), account.getPassword());
        if(authenticated == null) {
            throw new UnAuthorizedUserException("Invalid username and/or password");
        }
        return authenticated;
    }

    /**
     * Validates an account object.
     * @param account the account to be validated
     * @return true if the account is valid (dose not include accountId, username and password are not null or empty, and password is >= 4)
     */
    private boolean validateAccount(Account account) {
        if((account.getAccountId() != null) || (account.getUsername() == null)
            || (account.getPassword() == null)) {
            return false;
        }
        if(account.getUsername().trim().isEmpty() || account.getPassword().trim().length() < 4) {
            return false;
        }
        return true;
    }
}
