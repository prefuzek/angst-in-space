(ns angst.library.planets)

(def planet-map
  {:Petiska {:name "Petiska" :colour "Black" :x 85 :y 48
  			 :connections [:Henz :Shoran :Kazo] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Henz {:name "Henz" :colour "Black" :x 195 :y 45
   		  :connections [:Echemmon :Chiu :Petiska  :Shoran] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Shoran {:name "Shoran" :colour "Black" :x 160 :y 180
   			:connections [:Echemmon :Odyssey :Petiska :Henz :Kazo] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Kazo {:name "Kazo" :colour "Black" :x 40 :y 200
   		  :connections [:Odyssey :Petiska :Shoran :Valeria] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Echemmon {:name "Echemmon" :colour "Black" :x 290 :y 185
   			  :connections [:Odyssey :Henz :Shoran :Chiu :Xosa :Caia :Altu] :ships 0 :ship-colour "Black" :production 5 :used false}
   :Odyssey {:name "Odyssey" :colour "Green" :x 205 :y 290
   			 :connections [:Valeria :Kazo :Shoran :Echemmon :Altu :Uchino :Thalia] :ships 1 :ship-colour "Green" :production 2 :used false}
   :Chiu {:name "Chiu" :colour "Black" :x 345 :y 40
   		  :connections [:Caia :Henz :Echemmon] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Caia {:name "Caia" :colour "Blue" :x 500 :y 85
   		  :connections [:Chiu :Xosa :Path :Quinz :Echemmon] :ships 1 :ship-colour "Blue" :production 1 :used false}
   :Xosa {:name "Xosa" :colour "Black" :x 485 :y 220
   		  :connections [:Echemmon :Caia :Algoa :Fignon :Path] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Altu {:name "Altu" :colour "Black" :x 375 :y 300
   		  :connections [:Xosa :Odyssey :Echemmon :Algoa :Thalia] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Valeria {:name "Valeria" :colour "Green" :x 35 :y 350
   			 :connections [:Kazo :Odyssey :Uchino :Zellner] :ships 1 :ship-colour "Green" :production 3 :used false}
   :Uchino {:name "Uchino" :colour "Black" :x 165 :y 390
   			:connections [:Valeria :Odyssey :Thalia :Zellner :Brahms] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Thalia {:name "Thalia" :colour "Black" :x 320 :y 390
   			:connections [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Kanolta {:name "Kanolta" :colour "Black" :x 435 :y 460
   			 :connections [:Thalia :Algoa :Salman :Tyson :Ryss] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Salman {:name "Salman" :colour "Black" :x 580 :y 510
   			:connections [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Zellner {:name "Zellner" :colour "Black" :x 40 :y 530
   			 :connections [:Valeria :Uchino :Brahms :Tomaso] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Brahms {:name "Brahms" :colour "Black" :x 195 :y 530
   			:connections [:Zellner :Uchino :Thalia :Tomaso :Tyson] :ships 0 :ship-colour "Black" :production 5 :used false}
   :Tomaso {:name "Tomaso" :colour "Black" :x 115 :y 640
   			:connections [:Zellner :Brahms :Tyson :Walden] :ships 0 :ship-colour "Black" :production 1 :used false}
   :Tyson {:name "Tyson" :colour "Black" :x 290 :y 590
   		   :connections [:Tomaso :Brahms :Thalia :Kanolta :Ryss :Walden] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Walden {:name "Walden" :colour "Red" :x 390 :y 710
   			:connections [:Tomaso :Tyson :Ryss :Bhowmik :Glushko] :ships 1 :ship-colour "Red" :production 2 :used false}
   :Ryss {:name "Ryss" :colour "Red" :x 425 :y 575
   		  :connections [:Walden :Tyson :Kanolta :Salman :Bhowmik] :ships 1 :ship-colour "Red" :production 3 :used false}
   :Fignon {:name "Fignon" :colour "Black" :x 600 :y 280
   			:connections [:Algoa :Jaid :Xosa :Lisst :Path] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Algoa {:name "Algoa" :colour "Black" :x 510 :y 350
   		   :connections [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman] :ships 0 :ship-colour "Black" :production 5 :used false}
   :VanVogt {:name "Van Vogt" :colour "Black" :x 785  :y 485
   			 :connections [:Jaid :Dengras :Entli :Erasmus :Iago :Froya] :ships 0 :ship-colour "Black" :production 5 :used false}
   :Jaid {:name "Jaid" :colour "Black" :x 660 :y 375
   		  :connections [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Bhowmik {:name "Bhowmik" :colour "Black" :x 560 :y 645
   			 :connections [:Walden :Ryss :Salman :Dengras :Glushko] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Dengras {:name "Dengras" :colour "Black" :x 690 :y 580
   			 :connections [:Glushko :Bhowmik :Salman :Jaid :VanVogt :Marishka :Entli] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Glushko {:name "Glushko" :colour "Black" :x 670 :y 700
   			 :connections [:Walden :Bhowmik :Dengras :Marishka] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Marishka {:name "Marishka" :colour "Black" :x 800 :y 665
   			  :connections [:Glushko :Dengras :Entli :Beek] :ships 0 :ship-colour "Black" :production 1 :used false}
   :Entli {:name "Entli" :colour "Black" :x 880 :y 590
   		   :connections [:Beek :Marishka :Dengras :VanVogt :Froya] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Beek {:name "Beek" :colour "Black" :x 940 :y 685
   		  :connections [:Marishka :Entli :Froya] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Froya {:name "Froya" :colour "Black" :x 1000 :y 550
   		   :connections [:Beek :Entli :Erasmus :VanVogt] :ships 0 :ship-colour "Black" :production 4 :used false}
   :Erasmus {:name "Erasmus" :colour "Yellow" :x 985 :y 380
   			 :connections [:Froya :VanVogt :Iago :Nussbaum] :ships 1 :ship-colour "Yellow" :production 3 :used false}
   :Iago {:name "Iago" :colour "Yellow" :x 850 :y 340
   		  :connections [:Erasmus :VanVogt :Jaid :Lisst :Nussbaum] :ships 1 :ship-colour "Yellow" :production 2 :used false}
   :Lisst {:name "Lisst" :colour "Black" :x 780 :y 245
   		   :connections [:Iago :Jaid :Fignon :Nussbaum :Path :Byrd :Yerba] :ships 0 :ship-colour "Black" :production 1 :used false}
   :Nussbaum {:name "Nussbaum" :colour "Black" :x 970 :y 250
   			  :connections [:Erasmus :Iago :Lisst] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Path {:name "Path" :colour "Blue" :x 640 :y 160
   		  :connections [:Fignon :Xosa :Caia :Quinz :Byrd :Lisst] :ships 1 :ship-colour "Blue" :production 4 :used false}
   :Quinz {:name "Quinz" :colour "Black" :x 620 :y 30
   		   :connections [:Path :Caia :Byrd] :ships 0 :ship-colour "Black" :production 3 :used false}
   :Byrd {:name "Byrd" :colour "Black" :x 800 :y 105
   		  :connections [:Lisst :Path :Quinz :Yerba] :ships 0 :ship-colour "Black" :production 2 :used false}
   :Yerba {:name "Yerba" :colour "Black" :x 925 :y 100
   		   :connections [:Nussbaum :Lisst :Byrd] :ships 0 :ship-colour "Black" :production 4 :used false}
   })