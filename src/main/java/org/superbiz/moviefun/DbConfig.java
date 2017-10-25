package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.movies.Movie;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
public class DbConfig {


    static class DataSourceConfig {
        String url;
        String password;
        String username;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    @Component
    @ConfigurationProperties("moviefun.datasources.movies")
    static class MoviesDataSouceConfig extends DataSourceConfig{

    }

    @Component
    @ConfigurationProperties("moviefun.datasources.albums")
    static class AlbumsDataSouceConfig extends DataSourceConfig{

    }

    @Bean("ds.albums")
    public DataSource albumsDataSource(AlbumsDataSouceConfig props) {
        HikariConfig cfg = new HikariConfig();
        cfg.setUsername(props.username);
        cfg.setJdbcUrl(props.url);
        cfg.setPassword(props.password);
        return new HikariDataSource(cfg);
    }

    @Bean("ds.movies")
    public DataSource moviesDataSource(MoviesDataSouceConfig props) {
        HikariConfig cfg = new HikariConfig();
        cfg.setUsername(props.username);
        cfg.setJdbcUrl(props.url);
        cfg.setPassword(props.password);
        return new HikariDataSource(cfg);
    }

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setDatabase(Database.MYSQL);
        return adapter;
    }


    @Bean("emf.albums")
    LocalContainerEntityManagerFactoryBean albumEMF(@Qualifier("ds.albums") DataSource ds, JpaVendorAdapter jpaVendorAdapter) {
        return getLocalContainerEntityManagerFactoryBean(ds, jpaVendorAdapter, "albums", Album.class);
    }

    @Bean("emf.movies")
    LocalContainerEntityManagerFactoryBean moviesEMF(@Qualifier("ds.movies") DataSource ds, JpaVendorAdapter jpaVendorAdapter) {
        return getLocalContainerEntityManagerFactoryBean(ds, jpaVendorAdapter, "movies", Movie.class);
    }

    @Bean("movies")
    PlatformTransactionManager moviesTransactionManager(@Qualifier("emf.movies") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    @Bean("albums")
    PlatformTransactionManager albumsTransactionManager(@Qualifier("emf.albums") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    private LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean(DataSource ds, JpaVendorAdapter jpaVendorAdapter, String name, Class<?> klass) {
        LocalContainerEntityManagerFactoryBean b = new LocalContainerEntityManagerFactoryBean();
        b.setPersistenceUnitName(name);
        b.setDataSource(ds);
        b.setJpaVendorAdapter(jpaVendorAdapter);
        b.setPackagesToScan(klass.getPackage().getName());
        return b;
    }


}
