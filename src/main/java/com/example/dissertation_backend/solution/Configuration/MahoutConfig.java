// package com.example.dissertation_backend.solution.Configuration;

// import com.mysql.cj.x.protobuf.MysqlxCrud.DataModel;
// import javax.sql.DataSource;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class MahoutConfig {

//   @Bean
//   public DataModel dataModel(DataSource dataSource) throws TasteException {
//     // Define the MySQLJDBCDataModel with your specific table and column names
//     MySQLJDBCDataModel jdbcDataModel = new MySQLJDBCDataModel(
//       dataSource,
//       "user_activities", // Table name
//       "user_id", // User ID column name
//       "product_id", // Product ID column name
//       "preference_value", // Preference value column (e.g., could be 1 for views, 5 for purchases)
//       "activity_timestamp" // Timestamp column (optional)
//     );

//     // ReloadFromJDBCDataModel allows for refreshing the data model at runtime
//     return new ReloadFromJDBCDataModel(jdbcDataModel);
//   }
// }
