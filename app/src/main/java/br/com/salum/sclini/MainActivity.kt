package br.com.salum.sclini
import android.os.Bundle
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Navy=Color(0xFF123B67); private val Blue=Color(0xFF1769AA); private val Cyan=Color(0xFF20B8CC)
class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{App()}}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun App(){
 val ctx= androidx.compose.ui.platform.LocalContext.current
 val dao=remember{RecordDatabase.get(ctx).dao()}
 val scope=rememberCoroutineScope()
 var page by remember{mutableStateOf("home")}
 var records by remember{mutableStateOf(emptyList<IniRecord>())}
 var active by remember{mutableStateOf(emptyList<IniRecord>())}
 var selected by remember{mutableStateOf<IniRecord?>(null)}
 LaunchedEffect(dao){dao.observeAll().collectLatest{records=it}}
 LaunchedEffect(dao){dao.observeActiveInterdictions().collectLatest{active=it}}
 MaterialTheme(colorScheme=lightColorScheme(primary=Blue,secondary=Cyan)){
 Scaffold(topBar={if(page!="home")TopAppBar(title={Text(title(page))},navigationIcon={IconButton(onClick={page="home";selected=null}){Icon(Icons.Default.ArrowBack,"Voltar")}})}){p->
 when(page){
 "home"->Home(Modifier.padding(p)){page=it}
 "form"->RecordForm(Modifier.padding(p)){record->scope.launch{dao.insert(record);dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),record.id,System.currentTimeMillis(),"CRIADO","Registro emitido",record.fromPerson));page="records"}}
 "active"->RecordList(Modifier.padding(p),"Interdições Ativas",active){selected=it;page="detail"}
 "records"->RecordList(Modifier.padding(p),"Registros",records){selected=it;page="detail"}
 "actions"->RecordList(Modifier.padding(p),"Ações / Tratativas",records.filter{it.status!="LIBERADA"}){selected=it;page="detail"}
 "indicators"->Indicators(Modifier.padding(p),records)
 "detail"->selected?.let{RecordDetail(Modifier.padding(p),it,dao){updated->scope.launch{dao.update(updated);dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),updated.id,System.currentTimeMillis(),updated.status,updated.status,updated.releaseName));selected=updated};page="detail"}}
 "help"->Help(Modifier.padding(p))
 else->Home(Modifier.padding(p)){page=it}
 }
 }}
}
private fun title(p:String)=when(p){"form"->"Novo registro";"active"->"Interdições ativas";"records"->"Consultar registros";"actions"->"Tratativas";"indicators"->"Indicadores";"detail"->"Detalhes / histórico";else->"Instruções"}
@Composable private fun Home(m:Modifier,onNav:(String)->Unit){
 Column(m.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally){
 Spacer(Modifier.height(14.dp));Icon(Icons.Default.Block,null,tint=Blue,modifier=Modifier.size(68.dp));Text("SALUM CONSTRUÇÕES",color=Navy,fontWeight=FontWeight.Bold);Text("SCL - INI",fontSize=30.sp,fontWeight=FontWeight.Bold,color=Navy);Text("Interdição • Notificação • Intervenção",color=Color.Gray)
 Spacer(Modifier.height(18.dp))
 NavCard("Novo registro","Emitir formulário digital",Icons.Default.Add,Blue){onNav("form")}
 NavCard("Interdições ativas","Acompanhar bloqueios e liberação formal",Icons.Default.Block,Color(0xFFB54735)){onNav("active")}
 NavCard("Consultar registros","Histórico e pesquisa",Icons.Default.Search,Navy){onNav("records")}
 NavCard("Acompanhamento","Tratativas e prazos",Icons.Default.TaskAlt,Blue){onNav("actions")}
 NavCard("Indicadores","Visão gerencial local",Icons.Default.BarChart,Blue){onNav("indicators")}
 NavCard("Instruções","Orientações e regras",Icons.Default.Info,Navy){onNav("help")}
 Spacer(Modifier.height(20.dp));Text("Versão 1.2.0",color=Color.Gray,fontSize=12.sp)
 }}
@Composable private fun NavCard(t:String,s:String,i: androidx.compose.ui.graphics.vector.ImageVector,c:Color,on:()->Unit){
 Card(onClick=on,Modifier.fillMaxWidth().padding(vertical=5.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically){Icon(i,null,tint=c,modifier=Modifier.size(30.dp));Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(t,fontWeight=FontWeight.Bold,color=Navy);Text(s,fontSize=12.sp,color=Color.Gray)};Icon(Icons.Default.ChevronRight,null)}}
}
@Composable private fun RecordForm(modifier:Modifier,onSave:(IniRecord)->Unit){
 val today=SimpleDateFormat("dd/MM/yyyy",Locale("pt","BR")).format(Date());val now=SimpleDateFormat("HH:mm",Locale("pt","BR")).format(Date())
 var date by remember{mutableStateOf(today)};var time by remember{mutableStateOf(now)};var loc by remember{mutableStateOf("")}
 var from by remember{mutableStateOf("")};var fromCo by remember{mutableStateOf("")};var to by remember{mutableStateOf("")};var toCo by remember{mutableStateOf("")}
 var area by remember{mutableStateOf("")};var type by remember{mutableStateOf("Interdição")};var desc by remember{mutableStateOf("")};var severity by remember{mutableStateOf("Moderada")}
 var rec by remember{mutableStateOf("")};var witness by remember{mutableStateOf("")};var sign by remember{mutableStateOf("")};var taken by remember{mutableStateOf("")};var obs by remember{mutableStateOf("")};var deadline by remember{mutableStateOf("")}
 var error by remember{mutableStateOf("")}
 LazyColumn(modifier.fillMaxSize().padding(16.dp)){item{
 Text("F-SSMA-0062R00 • Digital",fontWeight=FontWeight.Bold,color=Navy);Text("Campos do formulário e complementos digitais",fontSize=12.sp,color=Color.Gray)
 Field("Data",date){date=it};Field("Hora",time){time=it};Field("Local",loc){loc=it};Field("De",from){from=it};Field("Gerência/Empresa (De)",fromCo){fromCo=it};Field("Para",to){to=it};Field("Gerência/Empresa (Para)",toCo){toCo=it};Field("Área / Equipamento / Atividade / Documento",area){area=it}
 Text("Tipo",fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("Interdição","Notificação","Intervenção").forEach{FilterChip(type==it,{type=it},label={Text(it)})}}
 Field("Descrição da condição não conforme",desc,{desc=it},true);Text("Severidade",fontWeight=FontWeight.Bold);Row{listOf("Leve","Moderada","Grave").forEach{FilterChip(severity==it,{severity=it},label={Text(it)})}}
 Field("Recomendações",rec,{rec=it},true);Field("Responsável ou testemunha",witness){witness=it};Field("Assinatura do Emitente (nome/registro digital)",sign){sign=it};Field("Providências tomadas",taken,{taken=it},true);Field("Observações",obs,{obs=it},true);Field("Prazo de execução",deadline){deadline=it}
 if(error.isNotBlank())Text(error,color=Color.Red)
 Button(onClick={if(loc.isBlank()||desc.isBlank()||from.isBlank()||type.isBlank()){error="Preencha Local, De e Descrição antes de salvar."}else{val id=UUID.randomUUID().toString();val status=if(type=="Interdição")"ATIVA" else "EM TRATATIVA";val stamp=System.currentTimeMillis();onSave(IniRecord(id,date,time,loc,from,fromCo,to,toCo,area,type,desc,severity,rec,witness,sign,taken,obs,deadline,"","","",status,stamp,stamp))}},modifier=Modifier.fillMaxWidth()){Text("Salvar registro")}
 Spacer(Modifier.height(24.dp))
 }}
}
@Composable private fun Field(label:String,value:String,onChange:(String)->Unit,multi:Boolean=false){
 Column(Modifier.fillMaxWidth().padding(vertical=5.dp)){Text(label,fontSize=13.sp,fontWeight=FontWeight.SemiBold);OutlinedTextField(value,onChange,modifier=Modifier.fillMaxWidth(),minLines=if(multi)3 else 1)}
}
@Composable private fun RecordList(modifier:Modifier,title:String,list:List<IniRecord>,onSelect:(IniRecord)->Unit){
 Column(modifier.fillMaxSize().padding(14.dp)){Text(title,fontSize=24.sp,fontWeight=FontWeight.Bold,color=Navy);if(list.isEmpty())Text("Nenhum registro encontrado.",Modifier.padding(12.dp),color=Color.Gray)
 LazyColumn{items(list){r->Card(onClick={onSelect(r)},Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(14.dp)){Text("${r.type} • ${r.status}",fontWeight=FontWeight.Bold,color=Navy);Text("${r.date} ${r.time} — ${r.location}");Text(r.description,maxLines=2);Text("Prazo: ${r.deadline.ifBlank{"—"}}",fontSize=12.sp,color=Color.Gray)}}}}}
}
@Composable private fun RecordDetail(modifier:Modifier,r:IniRecord,dao:RecordDao,onUpdate:(IniRecord)->Unit){
 val ctx= androidx.compose.ui.platform.LocalContext.current;val scope=rememberCoroutineScope()
 var action by remember{mutableStateOf("")};var responsible by remember{mutableStateOf("")};var evidence by remember{mutableStateOf("")};var releaseName by remember{mutableStateOf("")};var releaseManagement by remember{mutableStateOf("")};var releaseSign by remember{mutableStateOf("")};var status by remember{mutableStateOf(r.status)}
 var events by remember{mutableStateOf(emptyList<RecordEvent>())}
 LaunchedEffect(r.id){dao.observeEvents(r.id).collectLatest{events=it}}
 Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)){
 Text("${r.type} • ${r.status}",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Navy);Text("${r.date} ${r.time} • ${r.location}");Text("Emitente: ${r.fromPerson} / ${r.fromCompany}");Text("Destinatário: ${r.toPerson} / ${r.toCompany}");Text("Área: ${r.areaEquipmentActivity}");Text("Severidade: ${r.severity}");Text("Descrição: ${r.description}");Text("Recomendações: ${r.recommendations}");Text("Providências: ${r.actionsTaken}");Text("Prazo: ${r.deadline}")
 Spacer(Modifier.height(12.dp));Text("Acompanhamento",fontWeight=FontWeight.Bold,color=Navy)
 Field("Tratativa / registro de andamento",action){action=it};Field("Responsável pela tratativa",responsible){responsible=it};Field("Referência de evidência (texto/URI)",evidence){evidence=it}
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={if(action.isNotBlank()){scope.launch{dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),r.id,System.currentTimeMillis(),"TRATATIVA",action,responsible,evidence))};onUpdate(r.copy(actionsTaken=action,status="EM TRATATIVA",updatedAt=System.currentTimeMillis()))}}){Text("Registrar tratativa")}}
 Text("Status",fontWeight=FontWeight.Bold);listOf("ATIVA","EM TRATATIVA","AGUARDANDO VERIFICAÇÃO").filter{r.type=="Interdição"}.forEach{FilterChip(status==it,{status=it},label={Text(it)})}
 if(r.type=="Interdição"&&r.status!="LIBERADA"){
 Text("Liberação formal",fontWeight=FontWeight.Bold,color=Color(0xFFB54735));Field("Nome do responsável pela liberação",releaseName){releaseName=it};Field("Gerência",releaseManagement){releaseManagement=it};Field("Registro de verificação / evidência",releaseSign){releaseSign=it}
 Button(onClick={if(releaseName.isNotBlank()&&releaseManagement.isNotBlank()&&releaseSign.isNotBlank()){val updated=r.copy(status="LIBERADA",releaseName=releaseName,releaseManagement=releaseManagement,releaseSignature=releaseSign,updatedAt=System.currentTimeMillis());scope.launch{dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),r.id,System.currentTimeMillis(),"LIBERAÇÃO FORMAL","Área liberada após verificação: "+releaseSign,releaseName,evidence))};onUpdate(updated)}},modifier=Modifier.fillMaxWidth()){Text("Confirmar liberação formal")};Text("A liberação exige nome, gerência e verificação/evidência. Não ocorre automaticamente.",fontSize=12.sp,color=Color.Gray)
 }
 Spacer(Modifier.height(10.dp));Text("Histórico",fontWeight=FontWeight.Bold,color=Navy);events.forEach{Text("• ${SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("pt","BR")).format(Date(it.timestamp))} — ${it.eventType}: ${it.description} (${it.responsible})",Modifier.padding(vertical=4.dp))}
 Button(onClick={exportPdf(ctx,r,events),modifier=Modifier.fillMaxWidth()){Text("Exportar PDF / compartilhar")}
 }
}
private fun exportPdf(context:android.content.Context,r:IniRecord,events:List<RecordEvent>){
 val doc=PdfDocument();val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create());val paint=Paint().apply{textSize=12f};var y=42f
 val lines=listOf("SALUM CONSTRUÇÕES LTDA.","SCL - INI • ${r.type}","F-SSMA-0062R00 • Registro ${r.id.take(8)}","Data/Hora: ${r.date} ${r.time}","Local: ${r.location}","De: ${r.fromPerson} • ${r.fromCompany}","Para: ${r.toPerson} • ${r.toCompany}","Área/Equipamento/Atividade: ${r.areaEquipmentActivity}","Tipo: ${r.type} • Severidade: ${r.severity}","Descrição: ${r.description}","Recomendações: ${r.recommendations}","Responsável/Testemunha: ${r.witness}","Emitente: ${r.emitterSignature}","Providências: ${r.actionsTaken}","Observações: ${r.observations}","Prazo: ${r.deadline}","Status: ${r.status}","Liberação - Nome: ${r.releaseName}","Gerência: ${r.releaseManagement}","Registro de verificação: ${r.releaseSignature}","HISTÓRICO"]+events.map{"${it.eventType}: ${it.description} — ${it.responsible}"}
 lines.forEach{line->val wrapped=line.chunked(85);wrapped.forEach{page.canvas.drawText(it,36f,y,paint);y+=18f;if(y>800){y=42f}}}
 doc.finishPage(page);val file=File(context.cacheDir,"SCL-INI-${r.id.take(8)}.pdf");file.outputStream().use{doc.writeTo(it)};doc.close()
 val uri=Uri.fromFile(file);val intent=Intent(Intent.ACTION_SEND).apply{type="application/pdf";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)};context.startActivity(Intent.createChooser(intent,"Compartilhar PDF"))
}
@Composable private fun Indicators(modifier:Modifier,records:List<IniRecord>){
 Column(modifier.fillMaxSize().padding(18.dp)){Text("Indicadores locais",fontSize=24.sp,fontWeight=FontWeight.Bold,color=Navy);val total=records.size;val interd=records.count{it.type=="Interdição"};val active=records.count{it.type=="Interdição"&&it.status!="LIBERADA"};val released=records.count{it.status=="LIBERADA"};val not=records.count{it.type=="Notificação"};val intervention=records.count{it.type=="Intervenção"}
 listOf("Total de registros" to total,"Interdições emitidas" to interd,"Interdições ativas" to active,"Interdições liberadas" to released,"Notificações" to not,"Intervenções" to intervention).forEach{(label,value)->Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Row(Modifier.padding(18.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(label);Text(value.toString(),fontWeight=FontWeight.Bold,color=Blue)}}}
 }}
@Composable private fun Help(modifier:Modifier){
 Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){Text("Instruções de uso",fontSize=24.sp,fontWeight=FontWeight.Bold,color=Navy);Text("• O app funciona localmente, sem necessidade de conexão.\n• Registre com clareza local, condição, recomendações e prazo.\n• Acompanhe tratativas no registro.\n• Interdições permanecem ativas até liberação formal.\n• A liberação requer responsável, gerência e evidência/verificação.\n• O histórico registra eventos de acompanhamento.\n• Exporte PDF para compartilhamento.\n• Campos adicionais digitais não substituem requisitos/procedimentos corporativos.")}}
