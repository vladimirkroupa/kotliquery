package kotliquery.action

import kotliquery.Query
import kotliquery.Session

data class UpdateAndReturnGeneratedKeyQueryAction(val query: Query) : QueryAction<Long?> {

    override fun runWithSession(session: Session): Long? {
        return session.updateAndReturnGeneratedKey(query)
    }
}