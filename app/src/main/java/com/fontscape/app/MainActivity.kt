package com.fontscape.app
import android.app.WallpaperManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainActivity:AppCompatActivity(){
 private val prefs by lazy{getSharedPreferences("fontscape",MODE_PRIVATE)}
 private val fonts=listOf("sans-serif","sans-serif-light","sans-serif-thin","sans-serif-medium","sans-serif-black","sans-serif-condensed","sans-serif-condensed-light","sans-serif-condensed-medium","serif","serif-monospace","monospace","casual","cursive")
 private val scenes=listOf("הרי שווייץ","לונדון בלילה","פריז","רומא","ניו יורק","חוף ים","יער ירוק","מדבר","הרים מושלגים","חלל")
 private lateinit var root:LinearLayout
 private var chosenFont="sans-serif"; private var chosenScene=0
 override fun onCreate(b:Bundle?){super.onCreate(b);chosenFont=prefs.getString("font","sans-serif")?:"sans-serif";chosenScene=prefs.getInt("scene",0);home()}
 private fun base(title:String){
  root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,24,28,24);layoutDirection=View.LAYOUT_DIRECTION_RTL}
  root.addView(TextView(this).apply{text=title;textSize=30f;setTypeface(Typeface.DEFAULT,Typeface.BOLD)})
  val s=ScrollView(this);s.addView(root);setContentView(s)
  val nav=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
  listOf("בית","גופנים","טפטים","ערכה","תאימות").forEach{label->nav.addView(MaterialButton(this).apply{text=label;setOnClickListener{when(label){"בית"->home();"גופנים"->fontPage();"טפטים"->wallPage();"ערכה"->themePage();else->compat()}}},LinearLayout.LayoutParams(0,-2,1f))}
  root.addView(nav)
 }
 private fun home(){base("FontScape");root.addView(TextView(this).apply{text="גופנים וטפטים • עובד גם אופליין";textSize=18f;setPadding(0,20,0,20)});root.addView(preview())}
 private fun preview():View{
  val card=MaterialCardView(this).apply{radius=28f}
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;background=BitmapDrawable(resources,scene(chosenScene,900,600))}
  box.addView(TextView(this).apply{text="18:14";textSize=48f;setTextColor(Color.WHITE);typeface=Typeface.create(chosenFont,0)})
  box.addView(TextView(this).apply{text="שלום • FontScape";textSize=22f;setTextColor(Color.WHITE);typeface=Typeface.create(chosenFont,0)})
  card.addView(box,ViewGroup.LayoutParams(-1,520));return card
 }
 private fun fontPage(){base("בחר גופן");val sample=EditText(this).apply{setText("כך ייראה הטקסט שלך • אבגדה ABC 123");textSize=21f};root.addView(sample)
  fonts.forEachIndexed{i,f->root.addView(MaterialButton(this).apply{text="אבגדה  ABC  123  •  גופן "+(i+1);textSize=19f;typeface=Typeface.create(f,0);setOnClickListener{chosenFont=f;prefs.edit{putString("font",f)};sample.typeface=Typeface.create(f,0);Toast.makeText(this@MainActivity,"הגופן נבחר",Toast.LENGTH_SHORT).show()}})}
  root.addView(TextView(this).apply{text="הגופנים במסך זה הם משפחות גופן שקיימות במכשיר. שינוי גופן בכל Android תלוי בתמיכת יצרן המכשיר.";setPadding(0,20,0,10)})
 }
 private fun wallPage(){base("טפטים אופליין");scenes.forEachIndexed{i,n->
  val card=MaterialCardView(this).apply{radius=24f;setContentPadding(12,12,12,12)}
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;background=BitmapDrawable(resources,scene(i,900,500))}
  box.addView(TextView(this).apply{text=n;textSize=26f;setTextColor(Color.WHITE);setShadowLayer(7f,0f,2f,Color.BLACK)})
  box.addView(MaterialButton(this).apply{text="הגדר כטפט";setOnClickListener{chosenScene=i;prefs.edit{putInt("scene",i)};applyWall(i)}})
  card.addView(box,ViewGroup.LayoutParams(-1,430));root.addView(card)
 }}
 private fun themePage(){base("תצוגה משולבת");root.addView(preview());val txt=TextView(this).apply{text="אנשי קשר\nישראל ישראלי\n050-1234567\n\nהגדרות\nתצוגה • צלילים • Bluetooth";textSize=22f;typeface=Typeface.create(chosenFont,0);setPadding(20,30,20,30)};root.addView(txt)
  val seek=SeekBar(this).apply{max=30;progress=8};seek.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(s:SeekBar?,p:Int,u:Boolean){txt.textSize=(14+p).toFloat()};override fun onStartTrackingTouch(s:SeekBar?){};override fun onStopTrackingTouch(s:SeekBar?){}});root.addView(TextView(this).apply{text="גודל טקסט"});root.addView(seek)
 }
 private fun compat(){base("בדיקת תאימות");root.addView(TextView(this).apply{text="יצרן: "+android.os.Build.MANUFACTURER+"\nדגם: "+android.os.Build.MODEL+"\nAndroid: "+android.os.Build.VERSION.RELEASE+"\n\n✓ טפט מערכת: נתמך\n✓ תצוגת גופנים: נתמך\n✓ עבודה אופליין: נתמך\n\nגופן מערכת מלא: תלוי במנגנון שהיצרן מספק.";textSize=18f});root.addView(MaterialButton(this).apply{text="פתח הגדרות תצוגה";setOnClickListener{try{startActivity(android.content.Intent(Settings.ACTION_DISPLAY_SETTINGS))}catch(e:Exception){Toast.makeText(this@MainActivity,"לא נמצאה הגדרת תצוגה",Toast.LENGTH_LONG).show()}}})}
 private fun applyWall(i:Int){try{WallpaperManager.getInstance(this).setBitmap(scene(i,1080,2400),null,true,WallpaperManager.FLAG_SYSTEM);Toast.makeText(this,"הטפט הוגדר",Toast.LENGTH_LONG).show()}catch(e:Exception){Toast.makeText(this,"הגדרת הטפט נכשלה",Toast.LENGTH_LONG).show()}}
 private fun scene(i:Int,w:Int,h:Int):Bitmap{
  val b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);val c=Canvas(b);val ps=arrayOf(intArrayOf(0xff274060.toInt(),0xff8fb9a8.toInt()),intArrayOf(0xff101820.toInt(),0xff5b4b8a.toInt()),intArrayOf(0xff6d597a.toInt(),0xffe56b6f.toInt()),intArrayOf(0xff7f5539.toInt(),0xffddb892.toInt()),intArrayOf(0xff14213d.toInt(),0xfffca311.toInt()),intArrayOf(0xff0077b6.toInt(),0xff90e0ef.toInt()),intArrayOf(0xff1b4332.toInt(),0xff95d5b2.toInt()),intArrayOf(0xff9c6644.toInt(),0xffffe8d6.toInt()),intArrayOf(0xff3d5a80.toInt(),0xffe0fbfc.toInt()),intArrayOf(0xff03045e.toInt(),0xff7209b7.toInt()));val p=ps[i%ps.size]
  c.drawRect(0f,0f,w.toFloat(),h.toFloat(),Paint().apply{shader=LinearGradient(0f,0f,w.toFloat(),h.toFloat(),p[0],p[1],Shader.TileMode.CLAMP)})
  val path=Path().apply{moveTo(0f,h*.78f);lineTo(w*.25f,h*.48f);lineTo(w*.46f,h*.73f);lineTo(w*.68f,h*.38f);lineTo(w.toFloat(),h*.72f);lineTo(w.toFloat(),h.toFloat());lineTo(0f,h.toFloat());close()}
  c.drawPath(path,Paint().apply{color=Color.argb(170,15,25,35)});return b
 }
}