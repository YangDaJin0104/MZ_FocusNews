package com.example.mz_focusnews.data;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager extends AsyncTask<Void, Void, Void> {

    @SuppressWarnings("deprecation")
    @Override
    protected Void doInBackground(Void... voids) {
        Connection conn = null;
        java.sql.Statement st = null;
        ResultSet rs = null;

        try {
            // 1. 드라이버 로딩
            Class.forName("com.mysql.jdbc.Driver");

            // 2. 연결하기
            String url = "jdbc:mysql://127.0.0.1:3306/mysql";
            conn = DriverManager.getConnection(url, "root", "1111");

            // 3. 쿼리 수행을 위한 Statement 객체 생성
            st = conn.createStatement();

            // 4. SQL 쿼리 작성
            /*String sql = "SELECT id, username FROM users";

            // 5. 쿼리 수행
            rs = stmt.executeQuery(sql);*/

            // 6. 실행결과 출력하기
            /*while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                Log.d("DatabaseManager", id + " " + username);
            }*/
            System.out.println("연결 성공");
        } catch (ClassNotFoundException e) {
            Log.e("DatabaseManager", "드라이버 로딩 실패");
            System.out.println("드라이버 로딩 실패");
        } catch (SQLException e) {
            Log.e("DatabaseManager", "에러 " + e);
            System.out.println("실패"+ e);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
                if (st != null && !st.isClosed()) {
                    st.close();
                }
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
