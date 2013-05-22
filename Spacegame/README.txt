General:
lightning tutorial: http://drilian.com/2009/02/25/lightning-bolts/
Open source sprites: http://www.lostgarden.com/2005/03/download-complete-set-of-sweet-8-bit.html

Info:
The game uses one thread which runs the game and the main (Swing thread) used for input processing. Active rendering, in combination with fullscreen mode,
is used to paint to the screen. Game objects are not separate threads but rather extend a common base class with an update() and draw() method, which will get called
every frame.