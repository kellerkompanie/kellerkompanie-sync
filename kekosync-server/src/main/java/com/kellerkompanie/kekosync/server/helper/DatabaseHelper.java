package com.kellerkompanie.kekosync.server.helper;

import com.kellerkompanie.kekosync.server.entities.SQLAddon;
import com.kellerkompanie.kekosync.server.entities.SQLAddonGroup;
import com.kellerkompanie.kekosync.server.entities.SQLAddonGroupMember;
import com.kellerkompanie.kekosync.server.entities.ServerConfig;

import java.sql.*;
import java.util.HashMap;

public class DatabaseHelper {

    private static DatabaseHelper instance;
    private Connection connection;

    private DatabaseHelper(ServerConfig serverConfig) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mariadb://localhost/kekosync", serverConfig.getDatabaseUser(), serverConfig.getDatabasePassword());
    }

    public static void setup(ServerConfig serverConfig) throws SQLException {
        instance = new DatabaseHelper(serverConfig);
    }

    public static DatabaseHelper getInstance() {
        if (instance == null)
            throw new IllegalStateException("you have to setup() DatabaseHelper before use");
        return instance;
    }

    public HashMap<String, SQLAddon> readAllAddonsFromDB() throws SQLException {
        HashMap<String, SQLAddon> sqlAddons = new HashMap<>();
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM addon")) {
                while (rs.next()) {
                    int id = rs.getInt("addon_id");
                    String uuid = rs.getString("addon_uuid");
                    String version = rs.getString("addon_version");
                    String foldername = rs.getString("addon_foldername");
                    String name = rs.getString("addon_name");

                    SQLAddon sqlAddon = new SQLAddon();
                    sqlAddon.setId(id);
                    sqlAddon.setUuid(uuid);
                    sqlAddon.setVersion(version);
                    sqlAddon.setFoldername(foldername);
                    sqlAddon.setName(name);

                    sqlAddons.put(uuid, sqlAddon);
                }
            }
        }
        return sqlAddons;
    }

    public HashMap<String, SQLAddonGroup> readAllAddonGroupsFromDB() throws SQLException {
        HashMap<String, SQLAddonGroup> sqlAddonGroups = new HashMap<>();
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM addon_group")) {
                while (rs.next()) {
                    int id = rs.getInt("addon_group_id");
                    String uuid = rs.getString("addon_group_uuid");
                    String version = rs.getString("addon_group_version");
                    String name = rs.getString("addon_group_name");
                    String author = rs.getString("addon_group_author");

                    SQLAddonGroup sqlAddonGroup = new SQLAddonGroup();
                    sqlAddonGroup.setId(id);
                    sqlAddonGroup.setUuid(uuid);
                    sqlAddonGroup.setVersion(version);
                    sqlAddonGroup.setName(name);
                    sqlAddonGroup.setAuthor(author);

                    sqlAddonGroups.put(uuid, sqlAddonGroup);
                }
            }
        }
        return sqlAddonGroups;
    }

    public HashMap<Integer, SQLAddonGroupMember> readAllAddonGroupMembersFromDB() throws SQLException {
        HashMap<Integer, SQLAddonGroupMember> sqlAddonGroupMembers = new HashMap<>();
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM addon_group_member")) {
                while (rs.next()) {
                    int addon_group_id = rs.getInt("addon_group_id");
                    int addon_id = rs.getInt("addon_id");

                    SQLAddonGroupMember sqlAddonGroupMember = new SQLAddonGroupMember();
                    sqlAddonGroupMember.setAddonGroupId(addon_group_id);
                    sqlAddonGroupMember.setAddonId(addon_id);

                    sqlAddonGroupMembers.put(addon_group_id, sqlAddonGroupMember);
                }
            }
        }
        return sqlAddonGroupMembers;
    }

    public int insertAddon(SQLAddon sqlAddon) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO addon VALUES (?,?,?,?)")) {
            preparedStatement.setString(1, sqlAddon.getUuid());
            preparedStatement.setString(2, sqlAddon.getVersion());
            preparedStatement.setString(3, sqlAddon.getFoldername());
            preparedStatement.setString(4, sqlAddon.getName());
            return preparedStatement.executeUpdate();
        }
    }
}
