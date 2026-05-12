package com.booking.bus.config;

import com.booking.bus.dao.*;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "com.booking.bus")
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/bus_booking");
        ds.setUsername("admin");
        ds.setPassword("admin");
        return ds;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.hbm2ddl.auto", "validate");
        return props;
    }

    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("com.booking.bus.entity");
        factoryBean.setHibernateProperties(hibernateProperties());
        try {
            factoryBean.afterPropertiesSet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory());
        return txManager;
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // Явное объявление DAO-бинов
    @Bean
    public BookingDAO bookingDAO() {
        return new BookingDAO(sessionFactory());
    }

    @Bean
    public ClientDAO clientDAO() {
        return new ClientDAO(sessionFactory());
    }

    @Bean
    public CompanyDAO companyDAO() {
        return new CompanyDAO(sessionFactory());
    }

    @Bean
    public FareDAO fareDAO() {
        return new FareDAO(sessionFactory());
    }

    @Bean
    public RouteDAO routeDAO() {
        return new RouteDAO(sessionFactory());
    }

    @Bean
    public SeatDAO seatDAO() {
        return new SeatDAO(sessionFactory());
    }

    @Bean
    public StopDAO stopDAO() {
        return new StopDAO(sessionFactory());
    }

    @Bean
    public TripDAO tripDAO() {
        return new TripDAO(sessionFactory());
    }
}