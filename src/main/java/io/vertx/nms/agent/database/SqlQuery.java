package io.vertx.nms.agent.database;

enum SqlQuery {

  // FACES
  CREATE_FACES_TABLE, ALL_FACES, GET_FACE, CREATE_FACE, SAVE_FACE, DELETE_FACE, DELETE_ALL_FACES, GET_FACE_BY_ID,

  // FIB
  CREATE_FIB_TABLE, ALL_FIB_ENTRIES, GET_FIB_ENTRY_BY_ID, CREATE_FIB_ENTRY, SAVE_FIB_ENTRY, DELETE_FIB_ENTRY, GET_FIB_BY_PREFIX,

  // Logs
  CREATE_LOGS_TABLE, ALL_LOGS, GET_LOG_BY_ID, CREATE_LOG, DELETE_LOG

}
