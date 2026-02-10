package com.bngs0.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.BitSet;
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SurvivorBird extends ApplicationAdapter {

    SpriteBatch batch;
    Texture backgorund, bird;
    Texture bee,bee2,bee3;
    float birdX,birdY = 0;
    int gameState; // oyunun başlayıp başlamama durumu
    int velocity = 0;
    float gravity = 3f;

    int numberOfEnemies = 4;
    float[] enemyX = new float[numberOfEnemies];
    float[] enemyOffset = new float[numberOfEnemies];
    float[] enemyOffset2 = new float[numberOfEnemies];
    float[] enemyOffset3 = new float[numberOfEnemies];
    Random random;
    float distance;
    float enemyVelocity = 15;

    // çarpışma algılamak için (collider)
    ShapeRenderer shapeRenderer;
    Circle birdCircle;
    Circle[] enemyCircle;
    Circle[] enemyCircle2;
    Circle[] enemyCircle3;

    //score
    int score = 0;
    int scoredEnemy = 0;
    BitmapFont font,gameOverFont;

    @Override
    public void create() { //start
        batch = new SpriteBatch();
        backgorund = new Texture("background.png");
        bird = new Texture("bird.png");
        bee = new Texture("bee.png");
        bee2 = new Texture("bee.png");
        bee3 = new Texture("bee.png");

        //kuşun başlangıç konumu
        birdX = Gdx.graphics.getWidth()/20;
        birdY = Gdx.graphics.getHeight()/2;

        shapeRenderer = new ShapeRenderer();

        birdCircle = new Circle();

        //enemy
        enemyCircle = new Circle[numberOfEnemies];
        enemyCircle2 = new Circle[numberOfEnemies];
        enemyCircle3 = new Circle[numberOfEnemies];

        distance = Gdx.graphics.getWidth()/2; // arılar arası mesafe

        random = new Random();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5);

        gameOverFont = new BitmapFont();
        gameOverFont.setColor(Color.WHITE);
        gameOverFont.getData().setScale(10);

        enemyInitialize();

    }

    private void enemyInitialize() {
        for (int i = 0; i < numberOfEnemies; i++) { // arıların oluşacağı yerlerin x konumları
            enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

            enemyX[i] = Gdx.graphics.getWidth() - bee.getWidth() / 2 + i * distance;

            enemyCircle[i] = new Circle();
            enemyCircle2[i] = new Circle();
            enemyCircle3[i] = new Circle();

        }
    }

    @Override
    public void render() { //update
        batch.begin();
        batch.draw(backgorund,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //bg'yi telefonun ölçülerine göre ayarladık

        if (gameState == 1){
            if (enemyX[scoredEnemy] < Gdx.graphics.getWidth()/20){
                score++;
                if (scoredEnemy < numberOfEnemies - 1){
                    scoredEnemy++;
                }else {
                    scoredEnemy = 0;
                }
            }

            if (Gdx.input.justTouched() && birdY < (Gdx.graphics.getHeight()- 200)){
                // kuşun uçması
                velocity = -Gdx.graphics.getHeight()/30;
            }

            enemySpawner();

            if (birdY > 0){
                // yer çekimi
                velocity += gravity;
                birdY -= velocity ;
            }else{
                gameState = 2;
            }
        } else if (gameState == 0){
            if (Gdx.input.justTouched()) {
                gameState = 1; // oyun başladı
            }
        } else if (gameState == 2){
            gameOverFont.draw(batch,"GAME OVER \n Tab To Play Again \n Score: "+score,Gdx.graphics.getWidth()/3.2f,Gdx.graphics.getHeight()/1.2f);

            // oyun bittiğinde ne olacak
            if (Gdx.input.justTouched()){ // öldüysem ve ekrana tekrar tıkladıysam
                gameState = 1;
                birdY = Gdx.graphics.getHeight()/2;
                enemyInitialize();
                velocity = 0;
                score = 0;
                scoredEnemy = 0;
            }

        }

        batch.draw(bird,birdX,birdY,Gdx.graphics.getWidth()/15, Gdx.graphics.getHeight()/10);
        font.draw(batch,String.valueOf(score),100,200);

        batch.end();

        birdCircle.set(birdX + Gdx.graphics.getWidth()/30,birdY + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30); // oluşturduğumuz circle şuan kuşun tam üstünde

        /*shaperendererları yorum satırına aldık çünkü artık circleların üzerlerinde olduğunu biliyoruz*/
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);*/

        for (int i = 0; i < numberOfEnemies; i++) {
            /*shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30,  Gdx.graphics.getHeight()/2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20,Gdx.graphics.getWidth() / 30);
            shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30,  Gdx.graphics.getHeight()/2 + enemyOffset2[i] + Gdx.graphics.getHeight() / 20,Gdx.graphics.getWidth() / 30);
            shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30,  Gdx.graphics.getHeight()/2 + enemyOffset3[i] + Gdx.graphics.getHeight() / 20,Gdx.graphics.getWidth() / 30);*/

            //çarpışma kontrol edicisi
            if (Intersector.overlaps(birdCircle,enemyCircle[i]) || Intersector.overlaps(birdCircle,enemyCircle2[i]) || Intersector.overlaps(birdCircle,enemyCircle3[i])){
                gameState = 2; // öldün demek
            }
        }
        //shapeRenderer.end();
    }
    private void enemySpawner() {
        for (int i = 0; i < numberOfEnemies; i++) { // arıların sürekli ve random oluşması için (spawner)
            if (enemyX[i] < 0){ // ekranın sonuna geldiysek arıyı başa al
                enemyX[i] += numberOfEnemies*distance;

                enemyOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                enemyOffset2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                enemyOffset3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
            }else{
                enemyX[i] -= enemyVelocity;

                batch.draw(bee,enemyX[i],Gdx.graphics.getHeight()/2 + enemyOffset[i],Gdx.graphics.getWidth()/15, Gdx.graphics.getHeight()/10);
                batch.draw(bee2,enemyX[i],Gdx.graphics.getHeight()/2 + enemyOffset2[i],Gdx.graphics.getWidth()/15, Gdx.graphics.getHeight()/10);
                batch.draw(bee3,enemyX[i],Gdx.graphics.getHeight()/2 + enemyOffset3[i],Gdx.graphics.getWidth()/15, Gdx.graphics.getHeight()/10);

                // enemy collider
                enemyCircle[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30,  Gdx.graphics.getHeight()/2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20,Gdx.graphics.getWidth() / 30);
                enemyCircle2[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30,  Gdx.graphics.getHeight()/2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20,Gdx.graphics.getWidth() / 30);
                enemyCircle3[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30,  Gdx.graphics.getHeight()/2 + enemyOffset[i] + Gdx.graphics.getHeight() / 20,Gdx.graphics.getWidth() / 30);


            }
        }
    }

    @Override
    public void dispose() {

    }
}
