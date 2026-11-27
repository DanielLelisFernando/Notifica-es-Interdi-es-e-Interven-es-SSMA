package br.com.salum.sclini
import android.os.Bundle
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Path as AndroidPath
import android.graphics.Bitmap
import android.util.Base64
import android.graphics.Typeface
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
 BackHandler(enabled=page!="home"){page=when(page){ "detail"->"records"; "form","records","active","backup","help"->"home";else->"home"};selected=null}
 var records by remember{mutableStateOf(emptyList<IniRecord>())}
 var active by remember{mutableStateOf(emptyList<IniRecord>())}
 var selected by remember{mutableStateOf<IniRecord?>(null)}
 LaunchedEffect(dao){dao.observeAll().collectLatest{records=it}}
 LaunchedEffect(dao){dao.observeActiveInterdictions().collectLatest{active=it}}
 MaterialTheme(colorScheme=lightColorScheme(primary=Blue,secondary=Cyan)){
 Scaffold(topBar={if(page!="home")TopAppBar(title={Text(title(page))},navigationIcon={IconButton(onClick={page=if(page=="detail")"records" else "home";selected=null}){Icon(Icons.Default.ArrowBack,"Voltar")}})}){p->
 when(page){
 "home"->Home(Modifier.padding(p)){page=it}
 "form"->RecordForm(Modifier.padding(p)){record->scope.launch{dao.insert(record);dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),record.id,System.currentTimeMillis(),"CRIADO","Registro emitido",record.fromPerson));page="records"}}
 "active"->RecordList(Modifier.padding(p),"Interdições Ativas",active){selected=it;page="detail"}
 "records"->RecordList(Modifier.padding(p),"Registros",records){selected=it;page="detail"}
 "detail"->selected?.let{RecordDetail(Modifier.padding(p),it,dao){updated->scope.launch{dao.update(updated);dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),updated.id,System.currentTimeMillis(),updated.status,updated.status,updated.releaseName));selected=updated};page="detail"}}
 "help"->Help(Modifier.padding(p))
 "backup"->BackupScreen(Modifier.padding(p),records)
 else->Home(Modifier.padding(p)){page=it}
 }
 }}
}
private fun title(p:String)=when(p){"form"->"Novo registro";"active"->"Interdições ativas";"records"->"Consultar registros";"detail"->"Detalhes do registro";else->"Instruções"}
@Composable private fun Home(m:Modifier,onNav:(String)->Unit){
 Column(m.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally){
 Spacer(Modifier.height(14.dp));Icon(Icons.Default.Block,null,tint=Blue,modifier=Modifier.size(68.dp));Text("SALUM CONSTRUÇÕES",color=Navy,fontWeight=FontWeight.Bold);Text("SCL - INI",fontSize=30.sp,fontWeight=FontWeight.Bold,color=Navy);Text("Interdição • Notificação • Intervenção",color=Color.Gray)
 Spacer(Modifier.height(18.dp))
 NavCard("Novo registro","Emitir formulário digital",Icons.Default.Add,Blue){onNav("form")}
 NavCard("Ativas","Interdições e notificações em andamento",Icons.Default.Block,Color(0xFFB54735)){onNav("active")}
 NavCard("Consulta","Pesquisar registros e filtrar por data",Icons.Default.Search,Navy){onNav("records")}
 NavCard("Exportar dados","Gerar arquivo ZIP de registros",Icons.Default.Archive,Blue){onNav("backup")}
 NavCard("Instruções","Orientações e regras",Icons.Default.Info,Navy){onNav("help")}
 Spacer(Modifier.height(20.dp));Text("Versão 1.2.1",color=Color.Gray,fontSize=12.sp)
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
 var photos by remember{mutableStateOf(emptyList<String>())}
 fun persistEvidence(uri:Uri):String { val file=File(ctx.filesDir,"evidence-${UUID.randomUUID()}.jpg");ctx.contentResolver.openInputStream(uri)?.use{input->file.outputStream().use{input.copyTo(it)}};return file.absolutePath }
 val gallery=rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()){uris->photos=photos+uris.mapNotNull{try{persistEvidence(it)}catch(_:Exception){null}}}
 val cameraUriState=remember{mutableStateOf<Uri?>(null)}
 val camera=rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){ok->if(ok&&cameraUriState.value!=null)photos=photos+try{persistEvidence(cameraUriState.value!!)}catch(_:Exception){cameraUriState.value.toString()}}
 fun launchCamera(){val file=File(ctx.cacheDir,"camera-${System.currentTimeMillis()}.jpg");val uri=FileProvider.getUriForFile(ctx,"br.com.salum.sclini.fileprovider",file);cameraUriState.value=uri;camera.launch(uri)}
 LazyColumn(modifier.fillMaxSize().padding(16.dp)){item{
 Text("F-SSMA-0062R00 • Digital",fontWeight=FontWeight.Bold,color=Navy);Text("Campos do formulário e complementos digitais",fontSize=12.sp,color=Color.Gray)
 var showDate by remember{mutableStateOf(false)};var showTime by remember{mutableStateOf(false)}
 Button(onClick={showDate=true},modifier=Modifier.fillMaxWidth()){Text("Selecionar data: $date")}
 Button(onClick={showTime=true},modifier=Modifier.fillMaxWidth()){Text("Selecionar hora: $time")}
 if(showDate){
  val ds=rememberDatePickerState()
  DatePickerDialog(onDismissRequest={showDate=false},confirmButton={TextButton(onClick={if(ds.selectedDateMillis!=null){date=SimpleDateFormat("dd/MM/yyyy",Locale("pt","BR")).format(Date(ds.selectedDateMillis!!));showDate=false}}){Text("Confirmar")}},dismissButton={TextButton(onClick={showDate=false}){Text("Cancelar")}}){DatePicker(state=ds)}
 }
 if(showTime){var hh by remember{mutableStateOf(12)};var mm by remember{mutableStateOf(0)};AlertDialog(onDismissRequest={showTime=false},title={Text("Selecionar horário")},text={Column{Text("Hora: $hh");Slider(value=hh.toFloat(),onValueChange={hh=it.toInt()},valueRange=0f..23f,steps=22);Text("Minuto: $mm");Slider(value=mm.toFloat(),onValueChange={mm=(it.toInt()/5)*5},valueRange=0f..55f,steps=10);Text(String.format(Locale("pt","BR"),"%02d:%02d",hh,mm))}},confirmButton={TextButton(onClick={time=String.format(Locale("pt","BR"),"%02d:%02d",hh,mm);showTime=false}){Text("Confirmar")}},dismissButton={TextButton(onClick={showTime=false}){Text("Cancelar")}})}
 Field("Local",loc,onChange={loc=it});Field("De",from,onChange={from=it});Field("Gerência/Empresa (De)",fromCo,onChange={fromCo=it});Field("Para",to,onChange={to=it});Field("Gerência/Empresa (Para)",toCo,onChange={toCo=it});Field("Área / Equipamento / Atividade / Documento",area,onChange={area=it})
 Text("Tipo",fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("Interdição","Notificação","Intervenção").forEach{FilterChip(type==it,{type=it},label={Text(it)})}}
 Field("Descrição da condição não conforme",desc,onChange={desc=it},multi=true);Text("Severidade",fontWeight=FontWeight.Bold);Row{listOf("Leve","Moderada","Grave").forEach{FilterChip(severity==it,{severity=it},label={Text(it)})}}
 Field("Recomendações",rec,onChange={rec=it},multi=true);Field("Responsável ou testemunha",witness,onChange={witness=it});Field("Nome/registro do Emitente",sign,onChange={sign=it}); Text("Assinatura manuscrita",fontWeight=FontWeight.Bold); SignaturePad(onSignature={sign=it}); Button(onClick={launchCamera()},modifier=Modifier.fillMaxWidth()){Text("Capturar foto pela câmera")}
 Button(onClick={gallery.launch(arrayOf("image/*"))},modifier=Modifier.fillMaxWidth()){Text("Adicionar fotos da galeria")}
 photos.forEachIndexed{idx,u->Row(verticalAlignment=Alignment.CenterVertically){Text("Evidência ${idx+1}: "+u.takeLast(32),fontSize=11.sp,modifier=Modifier.weight(1f));TextButton(onClick={photos=photos.filterIndexed{j,_->j!=idx}}){Text("Remover")}}};Field("Providências tomadas",taken,onChange={taken=it},multi=true);Field("Observações",obs,onChange={obs=it},multi=true);Field("Prazo de execução",deadline,onChange={deadline=it})
 if(error.isNotBlank())Text(error,color=Color.Red)
 Button(onClick={if(loc.isBlank()||desc.isBlank()||from.isBlank()||type.isBlank()){error="Preencha Local, De e Descrição antes de salvar."}else{val id=UUID.randomUUID().toString();val status=if(type=="Interdição")"ATIVA" else "REGISTRADO";val stamp=System.currentTimeMillis();onSave(IniRecord(id,date,time,loc,from,fromCo,to,toCo,area,type,desc,severity,rec,witness,sign,taken,obs+"\n[EVIDENCIAS]"+photos.joinToString("|"),deadline,"","","",status,stamp,stamp))}},modifier=Modifier.fillMaxWidth()){Text("Salvar registro")}
 Spacer(Modifier.height(24.dp))
 }}
}

@Composable private fun SignaturePad(onSignature:(String)->Unit){
 var strokes by remember{mutableStateOf(listOf<List<androidx.compose.ui.geometry.Offset>>())}; var current by remember{mutableStateOf(listOf<androidx.compose.ui.geometry.Offset>())}
 var widthForSignature by remember{mutableStateOf(600f)};var heightForSignature by remember{mutableStateOf(220f)}
 fun encodeSignature(all:List<List<androidx.compose.ui.geometry.Offset>>):String{
  val bitmap=Bitmap.createBitmap(600,220,Bitmap.Config.ARGB_8888);bitmap.eraseColor(AndroidColor.WHITE)
  val canvas=AndroidCanvas(bitmap);val p=Paint().apply{color=AndroidColor.BLACK;style=Paint.Style.STROKE;strokeWidth=5f;strokeCap=Paint.Cap.ROUND;strokeJoin=Paint.Join.ROUND}
  all.forEach{pts->if(pts.size>1){val path=AndroidPath();path.moveTo(pts[0].x*600f/widthForSignature,pts[0].y*220f/heightForSignature);pts.drop(1).forEach{path.lineTo(it.x*600f/widthForSignature,it.y*220f/heightForSignature)};canvas.drawPath(path,p)}}
  val bytes=java.io.ByteArrayOutputStream();bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);bitmap.recycle();return "SIGIMG:"+Base64.encodeToString(bytes.toByteArray(),Base64.NO_WRAP)
 }
 Column{Canvas(Modifier.fillMaxWidth().height(180.dp).padding(4.dp).pointerInput(Unit){detectDragGestures(onDragStart={current=listOf(it)},onDragEnd={strokes=strokes+listOf(current);current=emptyList()},onDrag={change,_->current=current+change.position;change.consume()})}){widthForSignature=size.width;heightForSignature=size.height;drawRect(Color.LightGray);(strokes+listOf(current)).forEach{pts->for(i in 1 until pts.size)drawLine(Color.Black,pts[i-1],pts[i],strokeWidth=4f)}}
  Button(onClick={onSignature(encodeSignature(strokes))},modifier=Modifier.fillMaxWidth()){Text("Salvar assinatura")}
  Row{TextButton(onClick={strokes=emptyList();current=emptyList();onSignature("")}){Text("Limpar assinatura")}}}
}
@Composable private fun Field(label:String,value:String,onChange:(String)->Unit,multi:Boolean=false){
 Column(Modifier.fillMaxWidth().padding(vertical=5.dp)){Text(label,fontSize=13.sp,fontWeight=FontWeight.SemiBold);OutlinedTextField(value,onChange,modifier=Modifier.fillMaxWidth(),minLines=if(multi)3 else 1)}
}
@Composable private fun RecordList(modifier:Modifier,title:String,list:List<IniRecord>,onSelect:(IniRecord)->Unit){
 var search by remember{mutableStateOf("")}
 var dateFilter by remember{mutableStateOf("")}
 var dateDialog by remember{mutableStateOf(false)}
 val filtered=list.filter { r ->
   (dateFilter.isBlank() || r.date == dateFilter) &&
   (search.isBlank() || r.location.contains(search,true) ||
    r.description.contains(search,true) || r.fromPerson.contains(search,true) ||
    r.toPerson.contains(search,true) || r.type.contains(search,true) ||
    r.status.contains(search,true))
 }
 Column(modifier.fillMaxSize().padding(14.dp)){
   Text(title,fontSize=24.sp,fontWeight=FontWeight.Bold,color=Navy)
   OutlinedTextField(search,{search=it},modifier=Modifier.fillMaxWidth(),
     label={Text("Pesquisar local, descrição, pessoa ou status")},
     leadingIcon={Icon(Icons.Default.Search,null)})
   Button(onClick={dateDialog=true},modifier=Modifier.fillMaxWidth()){
     Text(if(dateFilter.isBlank()) "Filtrar por data (calendário)" else "Data: $dateFilter • alterar")
   }
   if(dateFilter.isNotBlank()) TextButton(onClick={dateFilter=""}){Text("Limpar filtro de data")}
   if(dateDialog){
     val dp=rememberDatePickerState()
     DatePickerDialog(onDismissRequest={dateDialog=false},
       confirmButton={TextButton(onClick={
         val millis=dp.selectedDateMillis
         if(millis!=null){dateFilter=SimpleDateFormat("dd/MM/yyyy",Locale("pt","BR")).format(Date(millis));dateDialog=false}
       }){Text("Aplicar")}},
       dismissButton={TextButton(onClick={dateDialog=false}){Text("Cancelar")}}){DatePicker(state=dp)}
   }
   if(filtered.isEmpty()) Text("Nenhum registro encontrado.",Modifier.padding(12.dp),color=Color.Gray)
   LazyColumn(modifier=Modifier.weight(1f)){items(filtered){r->
     Card(onClick={onSelect(r)},Modifier.fillMaxWidth().padding(vertical=5.dp)){
       Column(Modifier.padding(14.dp)){
         Text("${r.type} • ${r.status}",fontWeight=FontWeight.Bold,color=Navy)
         Text("${r.date} ${r.time} — ${r.location}")
         Text(r.description,maxLines=2)
         Text("Prazo: ${r.deadline.ifBlank{"—"}}",fontSize=12.sp,color=Color.Gray)
       }
     }
   }}
 }
}
@Composable private fun RecordDetail(modifier:Modifier,r:IniRecord,dao:RecordDao,onUpdate:(IniRecord)->Unit){
 val ctx= androidx.compose.ui.platform.LocalContext.current;val scope=rememberCoroutineScope()
var releaseName by remember{mutableStateOf("")};var releaseManagement by remember{mutableStateOf("")};var releaseSign by remember{mutableStateOf("")};var status by remember{mutableStateOf(r.status)}
 var events by remember{mutableStateOf(emptyList<RecordEvent>())}
 var photoUri by remember{mutableStateOf("")}
 val photoPicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->uri?.let{photoUri=it.toString();try{ctx.contentResolver.takePersistableUriPermission(it,Intent.FLAG_GRANT_READ_URI_PERMISSION)}catch(_:Exception){};scope.launch{dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),r.id,System.currentTimeMillis(),"EVIDÊNCIA/FOTO","Arquivo anexado: $it","",it.toString()))}}}
 LaunchedEffect(r.id){dao.observeEvents(r.id).collectLatest{events=it}}
 Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)){
 Text("${r.type} • ${r.status}",fontSize=23.sp,fontWeight=FontWeight.Bold,color=Navy);Text("${r.date} ${r.time} • ${r.location}");Text("Emitente: ${r.fromPerson} / ${r.fromCompany}");Text("Destinatário: ${r.toPerson} / ${r.toCompany}");Text("Área: ${r.areaEquipmentActivity}");Text("Severidade: ${r.severity}");Text("Descrição: ${r.description}");Text("Recomendações: ${r.recommendations}");Text("Providências: ${r.actionsTaken}");Text("Prazo: ${r.deadline}")
 Button(onClick={photoPicker.launch(arrayOf("image/*"))},modifier=Modifier.fillMaxWidth()){Text("Anexar foto/evidência")}
 if(photoUri.isNotBlank())Text("Foto selecionada: $photoUri",fontSize=12.sp,color=Color.Gray)
 Text("Status",fontWeight=FontWeight.Bold);listOf("ATIVA","LIBERADA").filter{r.type=="Interdição"&&it!="LIBERADA"}.forEach{FilterChip(status==it,{status=it; onUpdate(r.copy(status=it,updatedAt=System.currentTimeMillis()))},label={Text(it)})}
 if(r.type=="Interdição"&&r.status!="LIBERADA"){
 Text("Liberação formal",fontWeight=FontWeight.Bold,color=Color(0xFFB54735));Field("Nome do responsável pela liberação",releaseName,onChange={releaseName=it});Field("Gerência",releaseManagement,onChange={releaseManagement=it});Field("Registro de verificação / evidência",releaseSign,onChange={releaseSign=it})
 Button(onClick={if(releaseName.isNotBlank()&&releaseManagement.isNotBlank()&&releaseSign.isNotBlank()){val updated=r.copy(status="LIBERADA",releaseName=releaseName,releaseManagement=releaseManagement,releaseSignature=releaseSign,updatedAt=System.currentTimeMillis());scope.launch{dao.insertEvent(RecordEvent(UUID.randomUUID().toString(),r.id,System.currentTimeMillis(),"LIBERAÇÃO FORMAL","Área liberada após verificação: "+releaseSign,releaseName,releaseSign))};onUpdate(updated)}},modifier=Modifier.fillMaxWidth()){Text("Confirmar liberação formal")};Text("A liberação exige nome, gerência e verificação/evidência. Não ocorre automaticamente.",fontSize=12.sp,color=Color.Gray)
 }
 Spacer(Modifier.height(10.dp))
 Button(onClick={exportPdf(ctx,r,events)},modifier=Modifier.fillMaxWidth()){Text("Exportar PDF / compartilhar")}
 }
}
private fun exportPdf(context:android.content.Context,r:IniRecord,events:List<RecordEvent>){
 val doc=PdfDocument(); val paint=Paint().apply{textSize=12f}; var pageNumber=1;var page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNumber).create());var y=42f
 fun newPage(){doc.finishPage(page);pageNumber++;page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNumber).create());y=42f}
 val headerPaint=Paint().apply{color=AndroidColor.rgb(18,59,103);textSize=20f;typeface=Typeface.create(Typeface.DEFAULT,Typeface.BOLD)};page.canvas.drawText("SALUM CONSTRUÇÕES",36f,y,headerPaint);y+=28f;page.canvas.drawText("SCL - INI • FORMULÁRIO F-SSMA-0062R00",36f,y,Paint().apply{textSize=13f;typeface=Typeface.create(Typeface.DEFAULT,Typeface.BOLD)});y+=24f;page.canvas.drawLine(36f,y,559f,y,Paint().apply{color=AndroidColor.rgb(32,184,204);strokeWidth=3f});y+=18f;val lines=listOf("Registro ${r.id.take(8)}","Data/Hora: ${r.date} ${r.time}","Local: ${r.location}","De: ${r.fromPerson} • ${r.fromCompany}","Para: ${r.toPerson} • ${r.toCompany}","Área/Equipamento/Atividade: ${r.areaEquipmentActivity}","Tipo: ${r.type} • Severidade: ${r.severity}","Descrição: ${r.description}","Recomendações: ${r.recommendations}","Responsável/Testemunha: ${r.witness}","Emitente: ${r.emitterSignature}","Providências: ${r.actionsTaken}","Observações: ${r.observations.substringBefore("[EVIDENCIAS]")}","Prazo: ${r.deadline}","Status: ${r.status}","Liberação - Nome: ${r.releaseName}","Gerência: ${r.releaseManagement}","Registro de verificação: ${r.releaseSignature}","")
 val eventLines=emptyList<String>()
 val allLines = lines + eventLines
 allLines.forEach{line->line.chunked(85).forEach{wrapped->if(y>790)newPage();page.canvas.drawText(wrapped,36f,y,paint);y+=18f}}
 // Assinatura manuscrita persistida como imagem Base64 (PNG).
 if(r.emitterSignature.startsWith("SIGIMG:")){
  try{val bytes=Base64.decode(r.emitterSignature.substringAfter("SIGIMG:"),Base64.DEFAULT);val bmp=BitmapFactory.decodeByteArray(bytes,0,bytes.size);if(bmp!=null){if(y>650)newPage();page.canvas.drawText("ASSINATURA MANUSCRITA DO EMITENTE",36f,y,paint);y+=12f;page.canvas.drawBitmap(bmp,null,RectF(36f,y,300f,y+100f),null);y+=110f;bmp.recycle()}}catch(_:Exception){}
 }
 // Evidências fotográficas inseridas no PDF.
 val marker="[EVIDENCIAS]"
 val evid=r.observations.substringAfter(marker,"").split("|").filter{it.startsWith("/")||it.startsWith("content://")||it.startsWith("file://")}
 evid.forEachIndexed{idx,raw->try{val bmp=if(raw.startsWith("/"))BitmapFactory.decodeFile(raw) else BitmapFactory.decodeStream(context.contentResolver.openInputStream(Uri.parse(raw)));if(bmp!=null){if(y>600)newPage();page.canvas.drawText("EVIDÊNCIA FOTOGRÁFICA ${idx+1}",36f,y,paint);y+=10f;val dest=RectF(36f,y,559f,y+240f);page.canvas.drawBitmap(bmp,null,dest,null);y+=250f;bmp.recycle()}}catch(_:Exception){if(y>790)newPage();page.canvas.drawText("Imagem não acessível: ${raw.take(55)}",36f,y,paint);y+=18f}}
 doc.finishPage(page);val file=File(context.cacheDir,"SCL-INI-${r.id.take(8)}.pdf");file.outputStream().use{doc.writeTo(it)};doc.close()
 val uri=FileProvider.getUriForFile(context,"br.com.salum.sclini.fileprovider",file)
 val intent=Intent(Intent.ACTION_SEND).apply{type="application/pdf";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)}
 context.startActivity(Intent.createChooser(intent,"Compartilhar PDF"))
}
@Composable private fun Help(modifier:Modifier){
 Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){Text("Instruções de uso",fontSize=24.sp,fontWeight=FontWeight.Bold,color=Navy);Text("• O app funciona localmente, sem necessidade de conexão.\n• Registre com clareza local, condição, recomendações e prazo.\n• Interdições permanecem ativas até liberação formal.\n• A liberação requer responsável, gerência e evidência/verificação.\n• O histórico registra eventos relevantes, incluindo evidências e liberações.\n• Exporte PDF para compartilhamento.\n• Campos adicionais digitais não substituem requisitos/procedimentos corporativos.")}}
@Composable private fun BackupScreen(modifier:Modifier,records:List<IniRecord>){
 val context=androidx.compose.ui.platform.LocalContext.current
 var msg by remember{mutableStateOf("")}
 Column(modifier.fillMaxSize().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("Exportação de dados",fontSize=24.sp,fontWeight=FontWeight.Bold,color=Navy)
 Text("Exporta registros cadastrados em arquivo JSON compactado (ZIP). Evidências fotográficas são armazenadas localmente no aplicativo.")
 Button(onClick={exportRecordsZip(context,records);msg="Arquivo ZIP gerado e aberto para compartilhamento."},modifier=Modifier.fillMaxWidth()){Text("Exportar ZIP")}
 if(msg.isNotBlank())Text(msg,color=Color.Gray)
 Text("Quantidade de registros: ${records.size}")
 }
}
private fun exportRecordsZip(context:android.content.Context,records:List<IniRecord>){
 fun esc(s:String)=s.replace("\\\\","\\\\\\\\").replace("\\\"","\\\\\\\"").replace("\\n","\\\\n")
 val json=records.joinToString(prefix="[",postfix="]"){r->"""{"id":"${esc(r.id)}","date":"${esc(r.date)}","time":"${esc(r.time)}","location":"${esc(r.location)}","fromPerson":"${esc(r.fromPerson)}","fromCompany":"${esc(r.fromCompany)}","toPerson":"${esc(r.toPerson)}","toCompany":"${esc(r.toCompany)}","area":"${esc(r.areaEquipmentActivity)}","type":"${esc(r.type)}","description":"${esc(r.description)}","severity":"${esc(r.severity)}","recommendations":"${esc(r.recommendations)}","witness":"${esc(r.witness)}","emitterSignature":"${esc(r.emitterSignature)}","actionsTaken":"${esc(r.actionsTaken)}","observations":"${esc(r.observations)}","deadline":"${esc(r.deadline)}","releaseName":"${esc(r.releaseName)}","releaseManagement":"${esc(r.releaseManagement)}","releaseSignature":"${esc(r.releaseSignature)}","status":"${esc(r.status)}","createdAt":${r.createdAt},"updatedAt":${r.updatedAt}}"""}
 val file=File(context.cacheDir,"SCL-INI-backup-${System.currentTimeMillis()}.zip")
 java.util.zip.ZipOutputStream(file.outputStream()).use{zip->zip.putNextEntry(java.util.zip.ZipEntry("records.json"));zip.write(json.toByteArray(Charsets.UTF_8));zip.closeEntry();zip.putNextEntry(java.util.zip.ZipEntry("README.txt"));zip.write("Backup SCL - INI. Exportação de registros; evidências anexadas via URI podem não estar incluídas.".toByteArray());zip.closeEntry()}
 val uri=FileProvider.getUriForFile(context,"br.com.salum.sclini.fileprovider",file);val intent=Intent(Intent.ACTION_SEND).apply{type="application/zip";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)};context.startActivity(Intent.createChooser(intent,"Compartilhar backup SCL - INI"))
}
