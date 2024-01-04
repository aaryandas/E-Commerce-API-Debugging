package org.yearup.data.mysql;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId){
       String sql = "SELECT * FROM profiles WHERE user_id = ? ";
       try(Connection conn = getConnection()){
           PreparedStatement ps = conn.prepareStatement(sql);
           ps.setInt(1,userId);

           ResultSet rs = ps.executeQuery();
           if(rs.next()){
               return(mapRow(rs));
           }

       }catch(SQLException ex){
           throw new RuntimeException();
       }
       return null;
    }

    @Override
    public void update(int userId, Profile profile){
        String sql = "UPDATE profiles SET first_name = ?,last_name = ?,phone = ?,email = ?,address = ?,city = ?,state = ?,zip = ? WHERE user_id = ?";
        try(Connection conn = getConnection()){
           PreparedStatement ps = conn.prepareStatement(sql);

           ps.setString(1,profile.getFirstName());
           ps.setString(2, profile.getLastName());
           ps.setString(3, profile.getPhone());
           ps.setString(4, profile.getEmail());
           ps.setString(5, profile.getAddress());
           ps.setString(6, profile.getCity());
           ps.setString(7, profile.getState());
           ps.setString(8, profile.getZip());
           ps.setInt(9,userId);

           ps.executeUpdate();

        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private Profile mapRow(ResultSet row) throws SQLException{
        int userId = row.getInt("user_id");
        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");

        return new Profile(userId,firstName,lastName,phone,email,address,city,state,zip);
    }

}
