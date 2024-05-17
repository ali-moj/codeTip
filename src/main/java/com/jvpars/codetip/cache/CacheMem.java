package com.jvpars.codetip.cache;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class CacheMem  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(unique = true)
    private String ssid;

    private String name;

    private Long roomId;



    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind
                    .ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
