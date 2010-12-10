package no.knowit.threads;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TaskTester {
	public static void main(String[] args) throws InterruptedException {
		new TaskTester().kjoer();
	}

	private void kjoer() throws InterruptedException {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
		Set<Future<Integer>> futures = Sets.newHashSet();
		for (int i = 0; i < 20; i++) {
			Future<Integer> future = fixedThreadPool.submit(new Jobb(i));
			futures.add(future);
		}
		System.out.println(String.format("Futures size: %d", futures.size()));
		fixedThreadPool.shutdown();

		List<ExecutionException> catchedExceptions = Lists.newArrayList();
		while (futures.size() > 0) {
			Future<Integer> futureToRemove = null;
			for (Future<Integer> future : futures) {
				if (future.isDone()) {
					try {
						//future.get throws ExecutionException || InterruptedException
						//Vår runtimexception blir wrappa i en ExecutionException, som er checked ;)
						Integer jobbnummer = future.get();
						System.out.println(String.format(
								"Returnert jobbnr: %d", jobbnummer));
					} catch (ExecutionException e) {
						catchedExceptions.add(e);
					}
					futureToRemove = future;
					break;
				}
			}
			if (futureToRemove != null) {
				futures.remove(futureToRemove);
				futureToRemove = null;
			}

		}

		System.out.println(String.format("Håndterte %d exceptions, de var: ",
				catchedExceptions.size()));
		for (ExecutionException executionException : catchedExceptions) {
			System.out.println(String.format("- %s",
					executionException.getMessage()));
		}
	}

	private class Jobb implements Callable<Integer> {
		int jobbnummer;

		public Jobb(int jobbnummer) {
			this.jobbnummer = jobbnummer;
		}

		@Override
		public Integer call() throws Exception {
			System.out.println(String.format(
					"Starter arbeidet med jobb nr: %d", jobbnummer));
			Thread.sleep(1000);
			if (jobbnummer == 5 || jobbnummer == 15) {
				throw new RuntimeException(String.format("Jobb nr: %d feilet",
						jobbnummer));
			}
			System.out.println(String.format("Ferdig med jobb nr: %d",
					jobbnummer));
			return jobbnummer;
		}

	}

}
