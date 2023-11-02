package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        int amount = 0;
        if (subscriptionType.equals(subscriptionType.BASIC)) {
            amount = 500 + 200 * subscription.getNoOfScreensSubscribed();
        } else if (subscriptionType.equals(subscriptionType.PRO)) {
            amount = 800 + 250 * subscription.getNoOfScreensSubscribed();
        } else {
            amount = 1000 + 350 * subscription.getNoOfScreensSubscribed();
        }

        subscription.setTotalAmountPaid(amount);

        subscription.setUser(user);

        user.setSubscription(subscription);

        subscriptionRepository.save(subscription);

        return amount;


    }

    public Integer upgradeSubscription(Integer userId) throws Exception {

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user=userRepository.findById(userId).get();

        Subscription subscription=user.getSubscription();

        SubscriptionType subscriptionType=subscription.getSubscriptionType();

        int prev=user.getSubscription().getTotalAmountPaid();

        int bal=0;

        if(subscriptionType.equals(SubscriptionType.ELITE)){
            throw  new Exception("Already the best Subscription");
        }else if(subscriptionType.equals(SubscriptionType.BASIC)){
            bal = 800 + 250 * subscription.getNoOfScreensSubscribed();
            subscription.setSubscriptionType(SubscriptionType.PRO);

        }else{
            bal=1000+350*subscription.getNoOfScreensSubscribed();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }

        subscriptionRepository.save(subscription);


        return bal-prev;



    }

    public Integer calculateTotalRevenueOfHotstar() {

        //We need to find out total Revenue of hotstar : from a

        List<Subscription> subscriptions=subscriptionRepository.findAll();

         int total=0;

        for (Subscription subscription:subscriptions){
            total+=subscription.getTotalAmountPaid();
        }
        return total;
    }
}