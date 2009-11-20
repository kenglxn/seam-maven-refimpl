/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.superbiz.logic;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.TestCase;
import no.knowit.test.openejb.OpenEjbBootStrap;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.superbiz.model.Movie;

public class MoviesTest extends TestCase {

  private Logger log = Logger.getLogger(this.getClass());

	protected static Context context;

	public MoviesTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		Properties p = new Properties();
		p.put("openejb.embedded.initialcontext.close", "destroy"); // http://blog.jonasbandi.net/2009/06/restarting-embedded-openejb-container.html
		p.put("log4j.category.org.superbiz.logic.MoviesTest", "debug"); 
		
		context = OpenEjbBootStrap.bootstrap(p);
	}

	@After
	public void tearDown() throws Exception {
		context.close();
	}

	private Movies createService() throws Exception {
		try {
			Object obj = context.lookup("moviesLocal");
			assertNotNull(obj);
			assertTrue(obj instanceof Movies);
			return (Movies) obj;
		}
		catch (NamingException e) {
			log.error(e);
			throw(e);
		}
	}
	
	@Test
	public void test() throws Exception {
		Movies movies = createService();
		movies.addMovie(new Movie("Quentin Tarantino", "Reservoir Dogs", 1992));
		movies.addMovie(new Movie("Joel Coen", "Fargo", 1996));
		movies.addMovie(new Movie("Joel Coen", "The Big Lebowski", 1998));

		List<Movie> list = movies.getMovies();
		assertEquals("List.size()", 3, list.size());

		for (Movie movie : list) {
			movies.deleteMovie(movie);
		}

		assertEquals("Movies.getMovies()", 0, movies.getMovies().size());
	}

}
