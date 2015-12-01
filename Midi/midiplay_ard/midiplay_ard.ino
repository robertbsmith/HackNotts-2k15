
#define CMD_PLAYNOTE  0x90  /* play a note: low nibble is generator #, note is next byte */
#define CMD_STOPNOTE  0x80  /* stop a note: low nibble is generator # */
#define CMD_RESTART   0xe0  /* restart the score from the beginning */
#define CMD_STOP      0xf0  /* stop playing */
#define OUTPUT_PIN    3

const unsigned int bfptr = 0;
unsigned int len = 0;
const unsigned int tune_frequencies2_PGM[128] =
{
    16,17,18,19,21,22,23,24,26,28,29,31,33,35,37,39,41,
    44,46,49,52,55,58,62,65,69,73,78,82,87,92,98,104,110,
    117,123,131,139,147,156,165,175,185,196,208,220,233,
    247,262,277,294,311,330,349,370,392,415,440,466,494,
    523,554,587,622,659,698,740,784,831,880,932,988,1047,
    1109,1175,1245,1319,1397,1480,1568,1661,1760,1865,1976,
    2093,2217,2349,2489,2637,2794,2960,3136,3322,3520,3729,
    3951,4186,4435,4699,4978,5274,5588,5920,6272,6645,7040,
    7459,7902,8372,8870,9397,9956,10548,11175,11840,12544,
    13290,14080,14917,15804,16744,17740,18795,19912,21096,
    22351,23680,25088};

byte midiBuffer[1024];

void playSong(const byte *score){
  const byte * score_start = score;
  int score_cursor = 0;
  byte cmd,opcode;
  unsigned duration;
  unsigned int frequency;
  byte note;
  int noteplaying = 0;
 
  while(1)
  {
    cmd = score[score_cursor++];
    opcode = cmd & 0xf0;
    if(cmd < 0x80 && noteplaying)
    {
      duration = ((unsigned)cmd << 8) | (score[score_cursor++]);
      delay(duration);
      Serial.write("\nDelaying");
    }
    else if(opcode == CMD_STOPNOTE){
      noTone(OUTPUT_PIN);
      Serial.write("\nStopping");
      noteplaying = 0;
    }
    else if(opcode == CMD_PLAYNOTE){
      note = score[score_cursor++];
      frequency = tune_frequencies2_PGM[note];
      tone(OUTPUT_PIN,frequency);
      noteplaying = 1;
      Serial.write("\nPlaying");
    }
    else if(opcode == CMD_RESTART){
      score_cursor = 0;
    }
    else if(opcode == CMD_STOP){
      break; // exit the while loop
    }   
  }
}

void serialEvent(){
// Should only receive midi file
while(Serial.available() == 0){}
len = (Serial.read() & 0xff) << 8;
while(Serial.available() == 0){}
len += Serial.read();

if(len > 1024)
  len = 1024;
int i = 0;
String string1 = String(len);
Serial.write("\ndata Received "); 
Serial.print(string1);
Serial.write(" bytes");
while(i < len-1)
{

  if(Serial.available() > 0)
  {
    i++;
    midiBuffer[i] = Serial.read();
  }
//  Serial.write("\n Received byte ");
//  Serial.print(String(i));
//  Serial.write(" : ");
//  Serial.print(midiBuffer[i]);
}

}

void setup() {
  // put your setup code here, to run once:
   Serial.begin(9600);

   Serial.write("Ready");

   tone(OUTPUT_PIN,2000);
   while(1);

}

void loop() {
  // put your main code here, to run repeatedly:
  if(len > 0)
  {
    Serial.write("\nPlaying");
    playSong(midiBuffer);
    len = 0;
  }
}
