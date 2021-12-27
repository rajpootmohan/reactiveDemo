package com.example.reactiveDemo.controller;

import com.example.reactiveDemo.serviceImpl.ElasticSearchServiceImpl;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class ESTestController {

    @Autowired
    private ElasticSearchServiceImpl elasticSearchServiceImpl;


    @GetMapping("/hit")
    public String hit() throws IOException {

        System.out.println("es controller enter");

        try {


            List<BoolQueryBuilder> boolQueryBuilders = new ArrayList<>();
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("vendor.name.keyword", "QQ"))
                    .must(QueryBuilders.matchQuery("isActive", 1))
                    .must(QueryBuilders.matchQuery("isDeleted", 0))
                    .must(QueryBuilders
                            .termsQuery("vendor.vendorExternalId.keyword", "12"));

            boolQueryBuilders.add(queryBuilder);


            BoolQueryBuilder query = new BoolQueryBuilder();

            for (BoolQueryBuilder boolQueryBuilder : boolQueryBuilders) {
                query.should(boolQueryBuilder);
            }
            SearchResponse esResponse = this.elasticSearchServiceImpl.search(
                    "hotel_room", "room", query, false,
                    10, 5000);
            SearchHit[] esRoomHits = esResponse.getHits().getHits();
        }
        catch (Exception e){
            System.out.println(e);
        }
        return "ok1";
    }


}
