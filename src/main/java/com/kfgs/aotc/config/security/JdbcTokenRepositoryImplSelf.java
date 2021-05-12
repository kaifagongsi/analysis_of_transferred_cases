package com.kfgs.aotc.config.security;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JdbcTokenRepositoryImplSelf extends JdbcDaoSupport implements PersistentTokenRepository {

    public static final String CREATE_TABLE_SQL = "create table aotc_persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)";
    public static final String DEF_TOKEN_BY_SERIES_SQL = "select username,series,token,last_used from aotc_persistent_logins where series = ?";
    public static final String DEF_INSERT_TOKEN_SQL = "insert into aotc_persistent_logins (username, series, token, last_used) values(?,?,?,?)";
    public static final String DEF_UPDATE_TOKEN_SQL = "update aotc_persistent_logins set token = ?, last_used = ? where series = ?";
    public static final String DEF_REMOVE_USER_TOKENS_SQL = "delete from aotc_persistent_logins where username = ?";
    private String tokensBySeriesSql = "select username,series,token,last_used from aotc_persistent_logins where series = ?";
    private String insertTokenSql = "insert into aotc_persistent_logins (username, series, token, last_used) values(?,?,?,?)";
    private String updateTokenSql = "update aotc_persistent_logins set token = ?, last_used = ? where series = ?";
    private String removeUserTokensSql = "delete from aotc_persistent_logins where username = ?";
    private boolean createTableOnStartup;

    public JdbcTokenRepositoryImplSelf() {

    }
    @Override
    protected void initDao() {
        if (this.createTableOnStartup) {
            this.getJdbcTemplate().execute("create table aotc_persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)");
        }

    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        this.getJdbcTemplate().update(this.insertTokenSql, new Object[]{token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate()});
    }
    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        this.getJdbcTemplate().update(this.updateTokenSql, new Object[]{tokenValue, lastUsed, series});
    }
    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            return (PersistentRememberMeToken)this.getJdbcTemplate().queryForObject(this.tokensBySeriesSql, new RowMapper<PersistentRememberMeToken>() {
                @Override
                public PersistentRememberMeToken mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));
                }
            }, new Object[]{seriesId});
        } catch (EmptyResultDataAccessException var3) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Querying token for series '" + seriesId + "' returned no results.", var3);
            }
        } catch (IncorrectResultSizeDataAccessException var4) {
            this.logger.error("Querying token for series '" + seriesId + "' returned more than one value. Series should be unique");
        } catch (DataAccessException var5) {
            this.logger.error("Failed to load token for series " + seriesId, var5);
        }

        return null;
    }
    @Override
    public void removeUserTokens(String username) {
        this.getJdbcTemplate().update(this.removeUserTokensSql, new Object[]{username});
    }
    public void setCreateTableOnStartup(boolean createTableOnStartup) {
        this.createTableOnStartup = createTableOnStartup;
    }
}
