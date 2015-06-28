package com.example.gamememory;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Jugar extends Activity {
	Button boton1;
	Button boton2;
	Button boton3;
	Button boton4;
	Button botones [];
	DibujarSecuencia hilo;
	int botonPulsado = -1;
	int nivel = 1;
	int [] secuencia = crearSecuencia();
	ArrayList <Integer> secuenciaUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jugar);
		
		boton1 = (Button)(findViewById(R.id.boton1));
		boton2 = (Button)(findViewById(R.id.boton2));
		boton3 = (Button)(findViewById(R.id.boton3));
		boton4 = (Button)(findViewById(R.id.boton4));
		botones = new Button[]{boton1,boton2,boton3,boton4};
		secuenciaUser = new ArrayList<Integer>();
		
		empezarPartida();
		for(int i = 0; i < 4; i++){
			final int aux = i;
			botones[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					secuenciaUser.add(aux+1);
					
					System.out.println("Tamaño secuencia del usuario: "+secuenciaUser.size());
					System.out.println("Nivel actual: "+nivel);
					if(secuenciaUser.size() == nivel){
						comprobarTirada(secuencia , secuenciaUser,hilo);
						secuenciaUser.removeAll(secuenciaUser);
					}
				}
			});
		}
	}
	
	
	public void empezarPartida(){
		nivel = 1;
		hilo = (DibujarSecuencia) new DibujarSecuencia().execute();
	}
	
	public void hasGanado(){
		AlertDialog.Builder alertaTimeOut = new Builder(this);
		alertaTimeOut.setTitle("Te sales de crack Gise.");
		alertaTimeOut.setMessage("Has rebentado el juego, Gise eres una crack :P");
		alertaTimeOut.setPositiveButton("Empezar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				secuencia = crearSecuencia();
				empezarPartida();
			}
		});
		
		alertaTimeOut.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		alertaTimeOut.setCancelable(false);
		alertaTimeOut.show();
	}
	
	public void hasPerdido(){
		AlertDialog.Builder alertaTimeOut = new Builder(this);
		alertaTimeOut.setTitle("You Lose.");
		alertaTimeOut.setMessage("Lo siento, has perdido.");
		alertaTimeOut.setPositiveButton("Empezar", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				secuencia = crearSecuencia();
				empezarPartida();
			}
		});
		alertaTimeOut.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		alertaTimeOut.setCancelable(false);
		alertaTimeOut.show();
	}
	
	public void comprobarTirada(int secuencia[],ArrayList secuenciaUser,DibujarSecuencia hilo){
		
		boolean pasarNivel = true;		
		
		for(int i=0; i<=nivel-1;i++){
			if(secuencia[i] != (Integer)secuenciaUser.get(i)){
				System.out.println(secuencia[i]+" = "+(Integer)secuenciaUser.get(i));
				pasarNivel=false;
			}
		}
		
		if(nivel == secuencia.length){
			hilo.cancel(true);
			hasGanado();
		}
		
		if(pasarNivel && nivel != secuencia.length){
			nivel++;
			hilo = (DibujarSecuencia) new DibujarSecuencia().execute();
		}
		
		if(!pasarNivel){
			hasPerdido();
		}
		
	}
	
	public void verSecuencia(int [] secuencia){
		for(int i=0;i<secuencia.length;i++){
			System.out.println(secuencia[i]);
		}
	}
	
	public int [] crearSecuencia(){
		int [] numeros = new int[10];
		for(int i=0;i<numeros.length;i++){
			numeros[i] = (int)(Math.random()*4+1);
		}
		return numeros;
	}

	
	
	//Hilo
	private class DibujarSecuencia extends AsyncTask<Void,Integer,Void>{
		int marcado;
		boolean limpiar=false;
		@Override
		protected void onPostExecute(Void result) {
			for(int i=0;i<botones.length;i++){
				botones[i].setBackgroundColor(Color.parseColor("#82FA58"));
				botones[i].setClickable(true);
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onPreExecute() {
			for(int i=0;i<botones.length;i++){
				botones[i].setBackgroundColor(Color.parseColor("#82FA58"));
				botones[i].setClickable(false);
			}
			super.onPreExecute();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			if(limpiar){
				//Deja los colores como estaban
				for(int i=0;i<botones.length;i++){
					botones[i].setBackgroundColor(Color.parseColor("#82FA58"));
				}
			}else{
				//Iluminamos recorrido 
				botones[botonPulsado].setBackgroundColor(Color.parseColor("#DF0101"));
			}
			
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			for(int i=0;i<nivel;i++){
				limpiar=true;
				botonPulsado=secuencia[i]-1;
				SystemClock.sleep(600);
				publishProgress();
				limpiar=false;
				SystemClock.sleep(600);
				publishProgress();
			}
			return null;
		}		
	}	
}
