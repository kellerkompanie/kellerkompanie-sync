package com.kellerkompanie.kekosync.server.helper;

import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.server.entities.SQLAddon;
import com.kellerkompanie.kekosync.server.entities.SQLAddonGroup;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class AddonProvider {

    private static AddonProvider instance;
    private HashSet<Mod> addons = new HashSet<>();
    private HashSet<ModGroup> addonGroups = new HashSet<>();

    private HashMap<String, SQLAddon> sqlAddons;
    private HashMap<String, SQLAddonGroup> sqlAddonGroups;

    private AddonProvider() {
        try {
            sqlAddons = DatabaseHelper.getInstance().readAllAddonsFromDB();
            sqlAddonGroups = DatabaseHelper.getInstance().readAllAddonGroupsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean containsAddon(String uuid) {
        return sqlAddons.containsKey(uuid);
    }

    /**
     * Returns the UUID of the addon specified by foldername, if such an addon exists in the database. Returns null
     * if no addon with the given foldername was found.
     *
     * @param foldername the name of the addon folder to look for
     * @return the UUID of the addon if one with the same foldername exists, null otherwise
     */
    public String getAddonUuidByFoldername(String foldername) {
        for (SQLAddon sqlAddon : sqlAddons.values()) {
            if (sqlAddon.getFoldername().equalsIgnoreCase(foldername))
                return sqlAddon.getUuid();
        }

        return null;
    }

    /**
     * adds a new addon that was not previously in the DB
     */
    public void createNewAddon(String uuid, String foldername) {
        log.info("found new addon, adding {} to database as {}", foldername, uuid);

        SQLAddon sqlAddon = new SQLAddon();
        sqlAddon.setId(-1);
        sqlAddon.setUuid(uuid);
        sqlAddon.setFoldername(foldername);
        sqlAddon.setName(foldername);
        sqlAddon.setVersion(generateVersionString());

        try {
            int insertId = DatabaseHelper.getInstance().insertAddon(sqlAddon);
            sqlAddon.setId(insertId);
            sqlAddons.put(uuid, sqlAddon);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateVersionString() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyymmdd-hhmmss");
        return dateFormat.format(date);
    }

    public static AddonProvider getInstance() {
        if (instance == null)
            instance = new AddonProvider();
        return instance;
    }

    public Set<Mod> getAllAddons() {
        return Collections.unmodifiableSet(addons);
    }

    public Set<ModGroup> getAllAddonGroups() {
        return Collections.unmodifiableSet(addonGroups);
    }
}
