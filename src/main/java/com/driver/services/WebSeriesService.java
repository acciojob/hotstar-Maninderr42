package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo


        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName())!=null){
            throw new Exception("Series is already present");
        }

        WebSeries series=new WebSeries();
        series.setSeriesName(webSeriesEntryDto.getSeriesName());
        series.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        series.setRating(webSeriesEntryDto.getRating());
        series.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse;

        try {
            productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        }catch (Exception e){
            throw new Exception("productionHouse not found");
        }

        series.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(series);

        double oldRating=productionHouse.getRatings();
        double  newrating=series.getRating();

        int size=productionHouse.getWebSeriesList().size();

        double update=oldRating+(newrating-oldRating)/size;

        productionHouse.setRatings(update);

        productionHouseRepository.save(productionHouse);

        WebSeries updatee=webSeriesRepository.save(series);

        return updatee.getId();


    }

}
