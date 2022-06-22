package songsms.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import com.netflix.discovery.EurekaClient;

/**
 * Gateway for the songsMS Microservice-ecosystem.
 * @author Julian Knepel
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Application {
	
	@Autowired
	private EurekaClient discoveryClient;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(p -> p
        		.path("/auth")
        		.uri(discoveryClient.getNextServerFromEureka("AUTH-SERVICE", false).getHomePageUrl()))
            .route(p -> p
        		.path("/songs", "/songs/{segment}", "/songs/playlists", "/songs/playlists/{segment}")
        		.uri(discoveryClient.getNextServerFromEureka("SONGS-SERVICE", false).getHomePageUrl()))
            .route(p -> p
        		.path("/albums", "/albums/{segment}")
        		.uri(discoveryClient.getNextServerFromEureka("ALBUMS-SERVICE", false).getHomePageUrl()))
        	.build();
    }

}
