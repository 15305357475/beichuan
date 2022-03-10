package org.fh.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * 说明：第一数据源配置
 * 作者：f-sci
 * 授权：bsic
 */
/**
 * @author Administrator
 *
 */
@Configuration
@MapperScan(basePackages = MasterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory") //扫描 Mapper 接口并容器管理
public class MasterDataSourceConfig {

    static final String PACKAGE = "org.fh.mapper.dsno1";										//master 目录
    static final String MAPPER_LOCATION1 = "classpath:mybatis/dsno1/*/*.xml";					//扫描的本系统 xml 目录
    static final String MAPPER_LOCATION2 = "classpath:META-INF/modeler-mybatis-mappings/*.xml";	//扫描的 model xml 目录
    static final String CONFIG_LOCATION = "classpath:mybatis/dsno1/mybatis-config.xml"; 		//自定义的mybatis config 文件位置
    static final String TYPE_ALIASES_PACKAGE = "org.fh.entity"; 								//扫描的 实体类 目录
 
    //TODO 解决创建流程时报act_de_model找不到
    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";
    
    @Value("${datasource.no1.url}")
    private String url;
 
    @Value("${datasource.no1.username}")
    private String user;
 
    @Value("${datasource.no1.password}")
    private String password;
 
    @Value("${datasource.no1.driver-class-name}")
    private String driverClass;
 
    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.addFilters("stat");
        dataSource.addFilters("wall");
        return dataSource;
    }
 
    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() throws SQLException {
        return new DataSourceTransactionManager(masterDataSource());
    }
 
    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(resolveMapperLocations());
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(MasterDataSourceConfig.CONFIG_LOCATION));
        sessionFactory.setTypeAliasesPackage(MasterDataSourceConfig.TYPE_ALIASES_PACKAGE);
        return sessionFactory.getObject();
    }
    
    /**加载多个mapper路径
     * @return
     */
    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add(MasterDataSourceConfig.MAPPER_LOCATION1);
        mapperLocations.add(MasterDataSourceConfig.MAPPER_LOCATION2);
        List<Resource> resources = new ArrayList<Resource>();
        if (mapperLocations != null) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean
    public Liquibase liquibase(DataSource dataSource) {
        Liquibase liquibase = null;
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            //TODO 解决创建流程时报act_de_model找不到
            database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("flowable");
            return liquibase;

        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating liquibase database", e);
        } finally {
            closeDatabase(liquibase);
        }
    }

    private void closeDatabase(Liquibase liquibase) {
        if (liquibase != null) {
            Database database = liquibase.getDatabase();
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                }
            }
        }
    }
    
}
