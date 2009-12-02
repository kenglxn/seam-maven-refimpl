package no.knowit.seam.model;

public class MyTransientClass extends ModelBase {

  private long opprettet;

  public MyTransientClass() { 
		opprettet = System.currentTimeMillis(); 
	}
	
  public long getHvorLengeHarJegLevd() { 
     return (long)System.currentTimeMillis() - opprettet; 
  }
}
