from PIL import Image
def concatImage(name, number, width, height):
	new_im = Image.new('RGBA', (width*number,height))
	zeros = "000"
	for i in xrange(1,number+1):
		im = Image.open(name+zeros+str(i)+".png")
		new_im.paste(im, ((i-1)*width,0))
		if i == 9:
			zeros = '00'
	new_im.save(name+'.png')
def concatImage(name1, name2, endname, width, height):
	new_im = Image.new('RGBA', (width,height*2))
	im = Image.open(name1+".png")
	new_im.paste(im, (0,0))
	im = Image.open(name2+".png")
	new_im.paste(im, (0,height))
	new_im.save(endname+'.png')
def concatImageDiffSizes(name1, name2, endname, width, height1, height2):
	new_im = Image.new('RGBA', (width,height1+height2))
	im = Image.open(name1+".png")
	new_im.paste(im, (0,0))
	im = Image.open(name2+".png")
	new_im.paste(im, (0,height1))
	new_im.save(endname+'.png')
#concatImage('human_swordsman', 55, 110, 70)
#concatImage('goblin_swordsman', 55, 110, 70)
#concatImage('shootenemy', 5, 35, 15)
#concatImage('shootplayer', 5, 35, 15)
#concatImage('goblin_archer', 'human_archer', 'sprite_archer', 3920, 50)
#concatImage('goblin_mage', 'human_mage', 'sprite_mage', 930, 34)
#concatImage('goblin_sheild', 'human_sheild', 'sprite_sheild', 6050, 70)
#concatImage('shootplayer', 'shootenemy', 'sprite_shoot', 175, 15)
#concatImage('shootplayeraoe', 'shootenemyaoe', 'sprite_shootaoe', 60, 60)
concatImageDiffSizes('sprite_sheild', 'sprite_enemies', 'sprite_enemies', 6050, 140, 168)