package com.web.map;

import java.util.ArrayList;

public class Model {
   ArrayList<Orgin> orgins=new ArrayList<>();
   ArrayList<Destination> destinations=new ArrayList<>();

    public Model(ArrayList<Orgin> orgins, ArrayList<Destination> destinations) {
        this.orgins = orgins;
        this.destinations = destinations;
    }

    public ArrayList<Orgin> getOrgins() {
        return orgins;
    }

    public void setOrgins(ArrayList<Orgin> orgins) {
        this.orgins = orgins;
    }

    public ArrayList<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }
}

