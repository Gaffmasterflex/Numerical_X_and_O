package wit.cgd.xando.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import wit.cgd.xando.game.WorldController;
import wit.cgd.xando.game.WorldRenderer;
import wit.cgd.xando.game.util.Constants;

public class GameScreen extends AbstractGameScreen implements InputProcessor{

	@SuppressWarnings("unused")
	private static final String TAG = GameScreen.class.getName();

	private WorldController     worldController;
	private WorldRenderer       worldRenderer;

	private boolean             paused;
	private Stage               stage;

	// MenuScreen widgets
	private Button              undoButton;
	private Button              hintButton;
	private InputProcessor		undoProcessor;
	private InputProcessor 		worldProcessor;

	// debug
	private final float         DEBUG_REBUILD_INTERVAL  = 5.0f;
	private boolean             debugEnabled            = false;
	private float               debugRebuildStage;

	private InputProcessor GameScreen;

	public GameScreen(Game game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClearColor(34 / 255.0f, 156 / 255.0f, 2 / 255.0f, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		worldRenderer.render();
		// Do not update game world when paused.

		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(deltaTime);
		}
		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
	}

	private void rebuildStage() {

		skin = new Skin(Gdx.files.internal(Constants.SKIN_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);

		// build all layers
		stack.add(buildUndoLayer());

	}

	private Table buildUndoLayer(){
		Table table = new Table();
		table.center().top();
		table.row();
		undoButton = new Button(skin, "undo");
		table.add(undoButton).pad(Constants.BUTTON_PAD);
		undoButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onUndoButtonClicked();
			}
		});
		return table;
	}

	//method undoes the positions on the board as many times as it's pressed,by using the stack in the board class.
	private void onUndoButtonClicked() {
		if(worldController.board.moves.isEmpty())return;

		boolean isEvenPlayer = (worldController.board.currentPlayer.mySymbol == worldController.board.playerSymbol.EVEN) ?
				true : false;

		int pos = worldController.board.moves.pop();
		int row = pos/3;
		int col = pos%3;

		boolean isEvenNumber = (worldController.board.cells[row][col] % 2 == 0) ? true: false;
		
		//go to the position that was last made and get number and add it back to the appropriate players list.
		if((isEvenPlayer && isEvenNumber) || (!isEvenPlayer && !isEvenNumber)){
			worldController.board.currentPlayer.numbers.add(worldController.board.cells[row][col]);
		}else{
			worldController.board.currentPlayer.opponent.numbers.add(worldController.board.cells[row][col]);
		}
		//take the number out of the used number list in the board class.
		worldController.board.usedNumbers.remove(worldController.board.usedNumbers.indexOf(worldController.board.cells[row][col]));
		//set this position back to 0 (i.e EMPTY)
		worldController.board.cells[row][col] = worldController.board.EMPTY; //set this position back to 0 (i.e EMPTY)
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void show() {
		worldController = new WorldController(game);
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
		stage = new Stage();
		InputProcessor worldProcessor = worldController;		//give worldcontroller a seperate processor
		InputProcessor uiProcessor = stage;						//give the ui (undo and hint buttons seperate processors
		InputMultiplexer multiplexer = new InputMultiplexer();	//use a multiplexer to deal with several processors
		multiplexer.addProcessor(uiProcessor);
		multiplexer.addProcessor(worldProcessor);
		Gdx.input.setInputProcessor(multiplexer);				//set input processor to the multiplexer.
		rebuildStage();
	}

	@Override
	public void hide() {
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		super.resume();
		paused = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}