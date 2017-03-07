package com.dbp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by sebastienschaeffler on 07/03/2017.
 */
@RestController
@RequestMapping("/sharePrices")
public class SharePriceRestController {

    @Autowired
    private SharePriceRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Collection<SharePrice>> getAllSharePrices(){
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<SharePrice> findById(@PathVariable Long id) {
        return new ResponseEntity<SharePrice>(repository.findById(id),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"name"})
    public ResponseEntity<Collection<SharePrice>> findBySharePriceName(@RequestParam(value="name") String name) {
        return new ResponseEntity<>(repository.findBySharePriceName(name), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addSharePrice(@RequestBody SharePrice input) {
        return new ResponseEntity<>(repository.save(input), HttpStatus.CREATED);
    }
}
