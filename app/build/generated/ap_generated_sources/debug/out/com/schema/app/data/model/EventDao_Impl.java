package com.schema.app.data.model;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EventDao_Impl implements EventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Event> __insertionAdapterOfEvent;

  private final EntityDeletionOrUpdateAdapter<Event> __deletionAdapterOfEvent;

  private final EntityDeletionOrUpdateAdapter<Event> __updateAdapterOfEvent;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllEvents;

  public EventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEvent = new EntityInsertionAdapter<Event>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `events` (`id`,`title`,`eventTimeMillis`,`preparationTimeMillis`,`travelTimeMillis`,`travelMode`,`address`,`latitude`,`longitude`,`notificationId`,`preparationNotificationTimeMillis`,`startPreparationTimeMillis`,`actualPreparationTimeMillis`,`actualTravelTimeMillis`,`isCompleted`,`totalPreparationTimeMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Event entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        statement.bindLong(3, entity.getEventTimeMillis());
        statement.bindLong(4, entity.getPreparationTimeMillis());
        statement.bindLong(5, entity.getTravelTimeMillis());
        if (entity.getTravelMode() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTravelMode());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAddress());
        }
        statement.bindDouble(8, entity.getLatitude());
        statement.bindDouble(9, entity.getLongitude());
        statement.bindLong(10, entity.getNotificationId());
        statement.bindLong(11, entity.getPreparationNotificationTimeMillis());
        statement.bindLong(12, entity.getStartPreparationTimeMillis());
        statement.bindLong(13, entity.getActualPreparationTimeMillis());
        statement.bindLong(14, entity.getActualTravelTimeMillis());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(15, _tmp);
        statement.bindLong(16, entity.getTotalPreparationTimeMillis());
      }
    };
    this.__deletionAdapterOfEvent = new EntityDeletionOrUpdateAdapter<Event>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `events` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Event entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEvent = new EntityDeletionOrUpdateAdapter<Event>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `events` SET `id` = ?,`title` = ?,`eventTimeMillis` = ?,`preparationTimeMillis` = ?,`travelTimeMillis` = ?,`travelMode` = ?,`address` = ?,`latitude` = ?,`longitude` = ?,`notificationId` = ?,`preparationNotificationTimeMillis` = ?,`startPreparationTimeMillis` = ?,`actualPreparationTimeMillis` = ?,`actualTravelTimeMillis` = ?,`isCompleted` = ?,`totalPreparationTimeMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Event entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        statement.bindLong(3, entity.getEventTimeMillis());
        statement.bindLong(4, entity.getPreparationTimeMillis());
        statement.bindLong(5, entity.getTravelTimeMillis());
        if (entity.getTravelMode() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTravelMode());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAddress());
        }
        statement.bindDouble(8, entity.getLatitude());
        statement.bindDouble(9, entity.getLongitude());
        statement.bindLong(10, entity.getNotificationId());
        statement.bindLong(11, entity.getPreparationNotificationTimeMillis());
        statement.bindLong(12, entity.getStartPreparationTimeMillis());
        statement.bindLong(13, entity.getActualPreparationTimeMillis());
        statement.bindLong(14, entity.getActualTravelTimeMillis());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(15, _tmp);
        statement.bindLong(16, entity.getTotalPreparationTimeMillis());
        statement.bindLong(17, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllEvents = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM events";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Event event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfEvent.insertAndReturnId(event);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Event event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfEvent.handle(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Event event) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfEvent.handle(event);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllEvents() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllEvents.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAllEvents.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Event>> getAllEvents() {
    final String _sql = "SELECT * FROM events WHERE isCompleted = 0 ORDER BY eventTimeMillis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"events"}, false, new Callable<List<Event>>() {
      @Override
      @Nullable
      public List<Event> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEventTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventTimeMillis");
          final int _cursorIndexOfPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationTimeMillis");
          final int _cursorIndexOfTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "travelTimeMillis");
          final int _cursorIndexOfTravelMode = CursorUtil.getColumnIndexOrThrow(_cursor, "travelMode");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfPreparationNotificationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationNotificationTimeMillis");
          final int _cursorIndexOfStartPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startPreparationTimeMillis");
          final int _cursorIndexOfActualPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualPreparationTimeMillis");
          final int _cursorIndexOfActualTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualTravelTimeMillis");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPreparationTimeMillis");
          final List<Event> _result = new ArrayList<Event>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Event _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final long _tmpEventTimeMillis;
            _tmpEventTimeMillis = _cursor.getLong(_cursorIndexOfEventTimeMillis);
            final long _tmpPreparationTimeMillis;
            _tmpPreparationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationTimeMillis);
            final long _tmpTravelTimeMillis;
            _tmpTravelTimeMillis = _cursor.getLong(_cursorIndexOfTravelTimeMillis);
            final String _tmpTravelMode;
            if (_cursor.isNull(_cursorIndexOfTravelMode)) {
              _tmpTravelMode = null;
            } else {
              _tmpTravelMode = _cursor.getString(_cursorIndexOfTravelMode);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final long _tmpPreparationNotificationTimeMillis;
            _tmpPreparationNotificationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationNotificationTimeMillis);
            final long _tmpStartPreparationTimeMillis;
            _tmpStartPreparationTimeMillis = _cursor.getLong(_cursorIndexOfStartPreparationTimeMillis);
            final long _tmpActualPreparationTimeMillis;
            _tmpActualPreparationTimeMillis = _cursor.getLong(_cursorIndexOfActualPreparationTimeMillis);
            final long _tmpActualTravelTimeMillis;
            _tmpActualTravelTimeMillis = _cursor.getLong(_cursorIndexOfActualTravelTimeMillis);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpTotalPreparationTimeMillis;
            _tmpTotalPreparationTimeMillis = _cursor.getLong(_cursorIndexOfTotalPreparationTimeMillis);
            _item = new Event(_tmpTitle,_tmpEventTimeMillis,_tmpPreparationTimeMillis,_tmpTravelTimeMillis,_tmpTravelMode,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpNotificationId,_tmpPreparationNotificationTimeMillis,_tmpStartPreparationTimeMillis,_tmpActualPreparationTimeMillis,_tmpActualTravelTimeMillis,_tmpIsCompleted,_tmpTotalPreparationTimeMillis);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Event>> getPastEvents() {
    final String _sql = "SELECT * FROM events WHERE isCompleted = 1 ORDER BY eventTimeMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"events"}, false, new Callable<List<Event>>() {
      @Override
      @Nullable
      public List<Event> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEventTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventTimeMillis");
          final int _cursorIndexOfPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationTimeMillis");
          final int _cursorIndexOfTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "travelTimeMillis");
          final int _cursorIndexOfTravelMode = CursorUtil.getColumnIndexOrThrow(_cursor, "travelMode");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfPreparationNotificationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationNotificationTimeMillis");
          final int _cursorIndexOfStartPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startPreparationTimeMillis");
          final int _cursorIndexOfActualPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualPreparationTimeMillis");
          final int _cursorIndexOfActualTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualTravelTimeMillis");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPreparationTimeMillis");
          final List<Event> _result = new ArrayList<Event>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Event _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final long _tmpEventTimeMillis;
            _tmpEventTimeMillis = _cursor.getLong(_cursorIndexOfEventTimeMillis);
            final long _tmpPreparationTimeMillis;
            _tmpPreparationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationTimeMillis);
            final long _tmpTravelTimeMillis;
            _tmpTravelTimeMillis = _cursor.getLong(_cursorIndexOfTravelTimeMillis);
            final String _tmpTravelMode;
            if (_cursor.isNull(_cursorIndexOfTravelMode)) {
              _tmpTravelMode = null;
            } else {
              _tmpTravelMode = _cursor.getString(_cursorIndexOfTravelMode);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final long _tmpPreparationNotificationTimeMillis;
            _tmpPreparationNotificationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationNotificationTimeMillis);
            final long _tmpStartPreparationTimeMillis;
            _tmpStartPreparationTimeMillis = _cursor.getLong(_cursorIndexOfStartPreparationTimeMillis);
            final long _tmpActualPreparationTimeMillis;
            _tmpActualPreparationTimeMillis = _cursor.getLong(_cursorIndexOfActualPreparationTimeMillis);
            final long _tmpActualTravelTimeMillis;
            _tmpActualTravelTimeMillis = _cursor.getLong(_cursorIndexOfActualTravelTimeMillis);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpTotalPreparationTimeMillis;
            _tmpTotalPreparationTimeMillis = _cursor.getLong(_cursorIndexOfTotalPreparationTimeMillis);
            _item = new Event(_tmpTitle,_tmpEventTimeMillis,_tmpPreparationTimeMillis,_tmpTravelTimeMillis,_tmpTravelMode,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpNotificationId,_tmpPreparationNotificationTimeMillis,_tmpStartPreparationTimeMillis,_tmpActualPreparationTimeMillis,_tmpActualTravelTimeMillis,_tmpIsCompleted,_tmpTotalPreparationTimeMillis);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<Event> getEventById(final int eventId) {
    final String _sql = "SELECT * FROM events WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, eventId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"events"}, false, new Callable<Event>() {
      @Override
      @Nullable
      public Event call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEventTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventTimeMillis");
          final int _cursorIndexOfPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationTimeMillis");
          final int _cursorIndexOfTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "travelTimeMillis");
          final int _cursorIndexOfTravelMode = CursorUtil.getColumnIndexOrThrow(_cursor, "travelMode");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
          final int _cursorIndexOfPreparationNotificationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationNotificationTimeMillis");
          final int _cursorIndexOfStartPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startPreparationTimeMillis");
          final int _cursorIndexOfActualPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualPreparationTimeMillis");
          final int _cursorIndexOfActualTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualTravelTimeMillis");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPreparationTimeMillis");
          final Event _result;
          if (_cursor.moveToFirst()) {
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final long _tmpEventTimeMillis;
            _tmpEventTimeMillis = _cursor.getLong(_cursorIndexOfEventTimeMillis);
            final long _tmpPreparationTimeMillis;
            _tmpPreparationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationTimeMillis);
            final long _tmpTravelTimeMillis;
            _tmpTravelTimeMillis = _cursor.getLong(_cursorIndexOfTravelTimeMillis);
            final String _tmpTravelMode;
            if (_cursor.isNull(_cursorIndexOfTravelMode)) {
              _tmpTravelMode = null;
            } else {
              _tmpTravelMode = _cursor.getString(_cursorIndexOfTravelMode);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpNotificationId;
            _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
            final long _tmpPreparationNotificationTimeMillis;
            _tmpPreparationNotificationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationNotificationTimeMillis);
            final long _tmpStartPreparationTimeMillis;
            _tmpStartPreparationTimeMillis = _cursor.getLong(_cursorIndexOfStartPreparationTimeMillis);
            final long _tmpActualPreparationTimeMillis;
            _tmpActualPreparationTimeMillis = _cursor.getLong(_cursorIndexOfActualPreparationTimeMillis);
            final long _tmpActualTravelTimeMillis;
            _tmpActualTravelTimeMillis = _cursor.getLong(_cursorIndexOfActualTravelTimeMillis);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpTotalPreparationTimeMillis;
            _tmpTotalPreparationTimeMillis = _cursor.getLong(_cursorIndexOfTotalPreparationTimeMillis);
            _result = new Event(_tmpTitle,_tmpEventTimeMillis,_tmpPreparationTimeMillis,_tmpTravelTimeMillis,_tmpTravelMode,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpNotificationId,_tmpPreparationNotificationTimeMillis,_tmpStartPreparationTimeMillis,_tmpActualPreparationTimeMillis,_tmpActualTravelTimeMillis,_tmpIsCompleted,_tmpTotalPreparationTimeMillis);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result.setId(_tmpId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Event getEventByIdDirect(final int eventId) {
    final String _sql = "SELECT * FROM events WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, eventId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfEventTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventTimeMillis");
      final int _cursorIndexOfPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationTimeMillis");
      final int _cursorIndexOfTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "travelTimeMillis");
      final int _cursorIndexOfTravelMode = CursorUtil.getColumnIndexOrThrow(_cursor, "travelMode");
      final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfNotificationId = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationId");
      final int _cursorIndexOfPreparationNotificationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "preparationNotificationTimeMillis");
      final int _cursorIndexOfStartPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startPreparationTimeMillis");
      final int _cursorIndexOfActualPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualPreparationTimeMillis");
      final int _cursorIndexOfActualTravelTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "actualTravelTimeMillis");
      final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
      final int _cursorIndexOfTotalPreparationTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPreparationTimeMillis");
      final Event _result;
      if (_cursor.moveToFirst()) {
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        final long _tmpEventTimeMillis;
        _tmpEventTimeMillis = _cursor.getLong(_cursorIndexOfEventTimeMillis);
        final long _tmpPreparationTimeMillis;
        _tmpPreparationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationTimeMillis);
        final long _tmpTravelTimeMillis;
        _tmpTravelTimeMillis = _cursor.getLong(_cursorIndexOfTravelTimeMillis);
        final String _tmpTravelMode;
        if (_cursor.isNull(_cursorIndexOfTravelMode)) {
          _tmpTravelMode = null;
        } else {
          _tmpTravelMode = _cursor.getString(_cursorIndexOfTravelMode);
        }
        final String _tmpAddress;
        if (_cursor.isNull(_cursorIndexOfAddress)) {
          _tmpAddress = null;
        } else {
          _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
        }
        final double _tmpLatitude;
        _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
        final double _tmpLongitude;
        _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
        final int _tmpNotificationId;
        _tmpNotificationId = _cursor.getInt(_cursorIndexOfNotificationId);
        final long _tmpPreparationNotificationTimeMillis;
        _tmpPreparationNotificationTimeMillis = _cursor.getLong(_cursorIndexOfPreparationNotificationTimeMillis);
        final long _tmpStartPreparationTimeMillis;
        _tmpStartPreparationTimeMillis = _cursor.getLong(_cursorIndexOfStartPreparationTimeMillis);
        final long _tmpActualPreparationTimeMillis;
        _tmpActualPreparationTimeMillis = _cursor.getLong(_cursorIndexOfActualPreparationTimeMillis);
        final long _tmpActualTravelTimeMillis;
        _tmpActualTravelTimeMillis = _cursor.getLong(_cursorIndexOfActualTravelTimeMillis);
        final boolean _tmpIsCompleted;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
        _tmpIsCompleted = _tmp != 0;
        final long _tmpTotalPreparationTimeMillis;
        _tmpTotalPreparationTimeMillis = _cursor.getLong(_cursorIndexOfTotalPreparationTimeMillis);
        _result = new Event(_tmpTitle,_tmpEventTimeMillis,_tmpPreparationTimeMillis,_tmpTravelTimeMillis,_tmpTravelMode,_tmpAddress,_tmpLatitude,_tmpLongitude,_tmpNotificationId,_tmpPreparationNotificationTimeMillis,_tmpStartPreparationTimeMillis,_tmpActualPreparationTimeMillis,_tmpActualTravelTimeMillis,_tmpIsCompleted,_tmpTotalPreparationTimeMillis);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
