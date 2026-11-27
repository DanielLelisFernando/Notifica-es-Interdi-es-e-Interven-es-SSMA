package br.com.salum.sclini
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName="records")
data class IniRecord(@PrimaryKey val id:String,val date:String,val time:String,val location:String,val fromPerson:String,val fromCompany:String,val toPerson:String,val toCompany:String,val areaEquipmentActivity:String,val type:String,val description:String,val severity:String,val recommendations:String,val witness:String,val emitterSignature:String,val actionsTaken:String,val observations:String,val deadline:String,val releaseName:String,val releaseManagement:String,val releaseSignature:String,val status:String,val createdAt:Long,val updatedAt:Long)
@Entity(tableName="record_events")
data class RecordEvent(@PrimaryKey val eventId:String,val recordId:String,val timestamp:Long,val eventType:String,val description:String,val responsible:String,val evidenceUri:String="")
@Dao interface RecordDao {
 @Query("SELECT * FROM records ORDER BY createdAt DESC") fun observeAll():Flow<List<IniRecord>>
 @Query("SELECT * FROM records WHERE status != 'LIBERADA' AND type='Interdição' ORDER BY createdAt DESC") fun observeActiveInterdictions():Flow<List<IniRecord>>
 @Query("SELECT * FROM records WHERE id=:id") suspend fun getById(id:String):IniRecord?
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insert(record:IniRecord)
 @Update suspend fun update(record:IniRecord)
 @Query("SELECT * FROM record_events WHERE recordId=:recordId ORDER BY timestamp ASC") fun observeEvents(recordId:String):Flow<List<RecordEvent>>
 @Insert suspend fun insertEvent(event:RecordEvent)
 @Query("SELECT * FROM records WHERE status!='LIBERADA' ORDER BY deadline ASC") fun observeOpenActions():Flow<List<IniRecord>>
}
@Database(entities=[IniRecord::class,RecordEvent::class],version=1,exportSchema=false)
abstract class RecordDatabase:RoomDatabase(){
 abstract fun dao():RecordDao
 companion object { @Volatile private var instance:RecordDatabase?=null
 fun get(context:Context):RecordDatabase=instance?: synchronized(this){instance?:Room.databaseBuilder(context.applicationContext,RecordDatabase::class.java,"scl-ini.db").build().also{instance=it}}
 }
}