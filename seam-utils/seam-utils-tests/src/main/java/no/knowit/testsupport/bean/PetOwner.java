package no.knowit.testsupport.bean;

import java.util.HashMap;
import java.util.Map;

public class PetOwner {
  private String name;
  private Map<String, Animal> pets = new HashMap<String, Animal>();
  
  private PetOwner() {
  }
  
  public String getName() {
    return name;
  }
  
  public Map<String, Animal> getPets() {
    return pets;
  }

  public static Builder owner(final String name) {
    return new Builder(name);
  }
  
  public static class Builder {
    private final PetOwner petOwner = new PetOwner();
    
    private Builder(final String name) {
      petOwner.name = name;
    }
    
    public Builder owns(Animal pet) {
      if(pet != null) {
        petOwner.pets.put(pet.name, pet);
      }
      return this;
    }
    
    public PetOwner build() {
      return petOwner;
    }
  }
}
