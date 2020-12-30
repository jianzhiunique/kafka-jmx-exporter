package io.github.jianzhiunique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaJmxExporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaJmxExporterApplication.class, args);
		/*Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(Integer.MAX_VALUE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		thread.setDaemon(true);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/

	}

}
