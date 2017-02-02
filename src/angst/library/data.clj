(ns angst.library.data)

(def init-state
  {:planets {}
   :display {:planet false
             :infobar-width 300}
   :empire {:Sheep {:name "Sheep" :colour "Blue" :resources 8 :vp 0 :major ""}
            :Gopher {:name "Gopher" :colour "Green" :resources 8 :vp 0 :major ""}
            :Muskox {:name "Muskox" :colour "Red" :resources 8 :vp 0 :major ""}
            :Llama {:name "Llama" :colour "Yellow" :resources 8 :vp 0 :major ""}}
   :active :Sheep
   :phase 0 ;0: Specialization, 1 Production, 2 Command, 3 Construction 
   ; Buttons coordinates given for center of button
   :buttons [{:label "End Specialization Phase"
              :x 1216
              :y 344 
              :width 200
              :height 50}
             {:label "Save"
              :x 900
              :y 40
              :width 80
              :height 40}
             {:label "Load"
              :x 800
              :y 40
              :width 80
              :height 40}
             {:label "Quit"
              :x 1000
              :y 40
              :width 80
              :height 40}]
   :commanding false ; Either false or planet keyword
   :ship-move false ; Either false or {:planet :numships}
   :next-player-map {}
   })

(def setup-state
  {:phase "setup"
   :options #{}
   :empires #{}
   :buttons [{:label "Start new game!" :x 683 :y 650 :width 300 :height 50}
         {:label "Load from save" :x 683 :y 580 :width 300 :height 50}
         {:label "Sheep Empire: No" :x 483 :y 250 :width 150 :height 40 :empire :Sheep :index 2}
         {:label "Gopher Empire: No" :x 483 :y 300 :width 150 :height 40 :empire :Gopher :index 3}
         {:label "Muskox Empire: No" :x 483 :y 350 :width 150 :height 40 :empire :Muskox :index 4}
         {:label "Llama Empire: No" :x 483 :y 400 :width 150 :height 40 :empire :Llama :index 5}
         {:label "Flamingo Empire: No" :x 483 :y 450 :width 150 :height 40 :empire :Flamingo :index 6}
         {:label "Random Start: Off" :x 883 :y 250 :width 150 :height 40 :option "rand-start" :index 7}
         {:label "Objectives: Off" :x 883 :y 300 :width 150 :height 40 :option "goals" :index 8}
         ]})

(def all-empires
  {:Sheep {:name "Sheep" :colour "Blue" :resources 8 :vp 0 :major ""}
     :Gopher {:name "Gopher" :colour "Green" :resources 8 :vp 0 :major ""}
     :Muskox {:name "Muskox" :colour "Red" :resources 8 :vp 0 :major ""}
     :Llama {:name "Llama" :colour "Yellow" :resources 8 :vp 0 :major ""}
     :Flamingo {:name "Flamingo" :colour "Pink" :resources 8 :vp 0 :major ""}})

(def planet-maps
  {5
    {:Petiska {:name "Petiska" :colour "Black" :x 85 :y 48
  			 :connections [:Henz :Shoran :Kazo] :ships 0 :moved 0 :ship-colour "Black"
  			 :production [1 1 2 2 3 3 3 4] :development -1 :used false}
   :Henz {:name "Henz" :colour "Black" :x 195 :y 45
   		  :connections [:Echemmon :Chiu :Petiska  :Shoran] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 1 1] :development -1 :used false}
   :Shoran {:name "Shoran" :colour "Black" :x 160 :y 180
   			:connections [:Echemmon :Odyssey :Petiska :Henz :Kazo] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 3 4 4 4 4 3] :development -1 :used false}
    :Kazo {:name "Kazo" :colour "Black" :x 40 :y 200
   		  :connections [:Odyssey :Petiska :Shoran :Valeria] :ships 0 :moved 0 :ship-colour "Black"
           :production [2 2 2 3 3 3 3 3] :development -1 :used false}
   :Echemmon {:name "Echemmon" :colour "Black" :x 290 :y 185
   			  :connections [:Odyssey :Henz :Shoran :Chiu :Xosa :Caia :Altu] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 3 4 4 5 6 5] :development -1 :used false}
   :Odyssey {:name "Odyssey" :colour "Black" :x 205 :y 290
   			 :connections [:Valeria :Kazo :Shoran :Echemmon :Altu :Uchino :Thalia] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 0 1 3 5 4 3 3] :development -1 :used false}
   :Chiu {:name "Chiu" :colour "Black" :x 345 :y 40
   		  :connections [:Caia :Henz :Echemmon :Quinz] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 2 2 3 3 3 3 3] :development -1 :used false}
   :Caia {:name "Caia" :colour "Black" :x 500 :y 85
   		  :connections [:Chiu :Xosa :Path :Quinz :Echemmon] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 1 1 1 1 1 1 1] :development -1 :used false}
   :Xosa {:name "Xosa" :colour "Black" :x 485 :y 220
   		  :connections [:Echemmon :Caia :Algoa :Fignon :Path :Altu] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 4 3 2 1] :development -1 :used false}
   :Altu {:name "Altu" :colour "Black" :x 375 :y 300
   		  :connections [:Xosa :Odyssey :Echemmon :Algoa :Thalia] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 2 2 2 3 4] :development -1 :used false}
   :Valeria {:name "Valeria" :colour "Black" :x 35 :y 350
   			 :connections [:Kazo :Odyssey :Uchino :Zellner] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 1 2 3 4 5 5 5] :development -1 :used false}
   :Uchino {:name "Uchino" :colour "Black" :x 165 :y 390
   			:connections [:Valeria :Odyssey :Thalia :Zellner :Brahms] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 2 2 3 3 3 3] :development -1 :used false}
   :Thalia {:name "Thalia" :colour "Black" :x 320 :y 390
   			:connections [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [5 4 4 3 3 3 2 2] :development -1 :used false}
   :Kanolta {:name "Kanolta" :colour "Black" :x 435 :y 460
   			 :connections [:Thalia :Algoa :Salman :Tyson :Ryss] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 3 4 4 4 4 4 5] :development -1 :used false}
   :Salman {:name "Salman" :colour "Black" :x 580 :y 510
   			:connections [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 1 4 2 2 2] :development -1 :used false}
   :Zellner {:name "Zellner" :colour "Black" :x 40 :y 530
   			 :connections [:Valeria :Uchino :Brahms :Tomaso] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 2 2 2 2 2 2 5] :development -1 :used false}
   :Brahms {:name "Brahms" :colour "Black" :x 195 :y 530
   			:connections [:Zellner :Uchino :Thalia :Tomaso :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 4 3 2 3 4 3 2] :development -1 :used false}
   :Tomaso {:name "Tomaso" :colour "Black" :x 115 :y 640
   			:connections [:Zellner :Brahms :Tyson :Walden] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 3 4 3 3 3 3] :development -1 :used false}
   :Tyson {:name "Tyson" :colour "Black" :x 290 :y 590
   		   :connections [:Tomaso :Brahms :Thalia :Kanolta :Ryss :Walden] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 0 0 0 6 7] :development -1 :used false}
   :Walden {:name "Walden" :colour "Black" :x 390 :y 710
   			:connections [:Tomaso :Tyson :Ryss :Bhowmik :Glushko] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 2 2 2 3 3 3] :development -1 :used false}
   :Ryss {:name "Ryss" :colour "Black" :x 425 :y 575
   		  :connections [:Walden :Tyson :Kanolta :Salman :Bhowmik] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 2 2] :development -1 :used false}
   :Fignon {:name "Fignon" :colour "Black" :x 600 :y 280
   			:connections [:Algoa :Jaid :Xosa :Lisst :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 4 5 4] :development -1 :used false}
   :Algoa {:name "Algoa" :colour "Black" :x 510 :y 350
   		   :connections [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 5 6 7] :development -1 :used false}
    :VanVogt {:name "Van Vogt" :colour "Black" :x 785  :y 485
   			 :connections [:Jaid :Dengras :Entli :Erasmus :Iago :Froya] :ships 0 :moved 0 :ship-colour "Black"
             :production [3 3 3 4 4 5 5 6] :development -1 :used false}
   :Jaid {:name "Jaid" :colour "Black" :x 660 :y 375
   		  :connections [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [3 3 3 3 3 3 3 3] :development -1 :used false}
   :Bhowmik {:name "Bhowmik" :colour "Black" :x 560 :y 645
   			 :connections [:Walden :Ryss :Salman :Dengras :Glushko] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 2 3 3 4 5 4 3] :development -1 :used false}
   :Dengras {:name "Dengras" :colour "Black" :x 690 :y 580
   			 :connections [:Glushko :Bhowmik :Salman :Jaid :VanVogt :Marishka :Entli] :ships 0 :moved 0 :ship-colour "Black" 
             :production [5 3 1 2 3 3 3 3] :development -1 :used false}
   :Glushko {:name "Glushko" :colour "Black" :x 670 :y 700
   			 :connections [:Walden :Bhowmik :Dengras :Marishka] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 3 1 3 1 3 1 3] :development -1 :used false}
   :Marishka {:name "Marishka" :colour "Black" :x 800 :y 665
   			  :connections [:Glushko :Dengras :Entli :Beek] :ships 0 :moved 0 :ship-colour "Black"
              :production [0 0 0 1 2 2 3 2] :development -1 :used false}
   :Entli {:name "Entli" :colour "Black" :x 880 :y 590
   		   :connections [:Beek :Marishka :Dengras :VanVogt :Froya] :ships 0 :moved 0 :ship-colour "Black"
            :production [4 2 3 3 2 1 3 2] :development -1 :used false}
   :Beek {:name "Beek" :colour "Black" :x 940 :y 685
   		  :connections [:Marishka :Entli :Froya] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 2 3 4 4 3 2 1] :development -1 :used false}
   :Froya {:name "Froya" :colour "Black" :x 1000 :y 550
   		   :connections [:Beek :Entli :Erasmus :VanVogt] :ships 0 :moved 0 :ship-colour "Black"
            :production [2 2 2 3 3 3 3 3] :development -1 :used false}
   :Erasmus {:name "Erasmus" :colour "Black" :x 985 :y 380
   			 :connections [:Froya :VanVogt :Iago :Nussbaum] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 3 2 4 3 5 4 2] :development -1 :used false}
   :Iago {:name "Iago" :colour "Black" :x 850 :y 340
   		  :connections [:Erasmus :VanVogt :Jaid :Lisst :Nussbaum] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 2 0 3 0 4 0 1] :development -1 :used false}
   :Lisst {:name "Lisst" :colour "Black" :x 780 :y 245
   		   :connections [:Iago :Jaid :Fignon :Nussbaum :Path :Byrd :Yerba] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 1 1 1 1 2 2] :development -1 :used false}
   :Nussbaum {:name "Nussbaum" :colour "Black" :x 970 :y 250
   			  :connections [:Erasmus :Iago :Lisst :Yerba] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 4 5 3 4 5 5] :development -1 :used false}
   :Path {:name "Path" :colour "Black" :x 640 :y 160
   		  :connections [:Fignon :Xosa :Caia :Quinz :Byrd :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 0 1 2 3 5 6 6] :development -1 :used false}
   :Quinz {:name "Quinz" :colour "Black" :x 620 :y 30
   		   :connections [:Path :Caia :Byrd :Chiu] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 3 4 3 3 4 3 3] :development -1 :used false}
   :Byrd {:name "Byrd" :colour "Black" :x 800 :y 105
   		  :connections [:Lisst :Path :Quinz :Yerba] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 3 2 2 2] :development -1 :used false}
   :Yerba {:name "Yerba" :colour "Black" :x 925 :y 100
   		   :connections [:Nussbaum :Lisst :Byrd] :ships 0 :moved 0 :ship-colour "Black"
            :production [4 3 2 1 1 1 1 0] :development -1 :used false}
   }
   4
   {:Henz {:name "Henz" :colour "Black" :x 195 :y 45
        :connections [:Echemmon :Chiu :Shoran] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 1 1] :development -1 :used false}
   :Shoran {:name "Shoran" :colour "Black" :x 160 :y 180
        :connections [:Echemmon :Odyssey :Henz] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 3 4 4 4 4 3] :development -1 :used false}
   :Echemmon {:name "Echemmon" :colour "Black" :x 290 :y 185
          :connections [:Odyssey :Henz :Shoran :Chiu :Xosa :Caia :Altu] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 3 4 4 5 6 5] :development -1 :used false}
   :Odyssey {:name "Odyssey" :colour "Black" :x 205 :y 290
         :connections [:Shoran :Echemmon :Altu :Uchino :Thalia] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 0 1 3 5 4 3 3] :development -1 :used false}
   :Chiu {:name "Chiu" :colour "Black" :x 345 :y 40
        :connections [:Caia :Henz :Echemmon :Quinz] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 2 2 3 3 3 3 3] :development -1 :used false}
   :Caia {:name "Caia" :colour "Black" :x 500 :y 85
        :connections [:Chiu :Xosa :Path :Quinz :Echemmon] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 1 1 1 1 1 1 1] :development -1 :used false}
   :Xosa {:name "Xosa" :colour "Black" :x 485 :y 220
        :connections [:Echemmon :Caia :Algoa :Fignon :Path :Altu] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 4 3 2 1] :development -1 :used false}
   :Altu {:name "Altu" :colour "Black" :x 375 :y 300
        :connections [:Xosa :Odyssey :Echemmon :Algoa :Thalia] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 2 2 2 3 4] :development -1 :used false}
   :Uchino {:name "Uchino" :colour "Black" :x 165 :y 390
        :connections [:Odyssey :Thalia :Brahms] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 2 2 3 3 3 3] :development -1 :used false}
   :Thalia {:name "Thalia" :colour "Black" :x 320 :y 390
        :connections [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [5 4 4 3 3 3 2 2] :development -1 :used false}
   :Kanolta {:name "Kanolta" :colour "Black" :x 435 :y 460
         :connections [:Thalia :Algoa :Salman :Tyson :Ryss] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 3 4 4 4 4 4 5] :development -1 :used false}
   :Salman {:name "Salman" :colour "Black" :x 580 :y 510
        :connections [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 1 4 2 2 2] :development -1 :used false}
   :Brahms {:name "Brahms" :colour "Black" :x 195 :y 530
        :connections [:Uchino :Thalia :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 4 3 2 3 4 3 2] :development -1 :used false}
   :Tyson {:name "Tyson" :colour "Black" :x 290 :y 590
         :connections [:Brahms :Thalia :Kanolta :Ryss :Walden] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 0 0 0 6 7] :development -1 :used false}
   :Walden {:name "Walden" :colour "Black" :x 390 :y 710
        :connections [:Tyson :Ryss :Bhowmik :Glushko] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 2 2 2 3 3 3] :development -1 :used false}
   :Ryss {:name "Ryss" :colour "Black" :x 425 :y 575
        :connections [:Walden :Tyson :Kanolta :Salman :Bhowmik] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 2 2] :development -1 :used false}
   :Fignon {:name "Fignon" :colour "Black" :x 600 :y 280
        :connections [:Algoa :Jaid :Xosa :Lisst :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 4 5 4] :development -1 :used false}
   :Algoa {:name "Algoa" :colour "Black" :x 510 :y 350
         :connections [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 5 6 7] :development -1 :used false}
    :VanVogt {:name "Van Vogt" :colour "Black" :x 785  :y 485
         :connections [:Jaid :Dengras :Entli :Erasmus :Iago] :ships 0 :moved 0 :ship-colour "Black"
             :production [3 3 3 4 4 5 5 6] :development -1 :used false}
   :Jaid {:name "Jaid" :colour "Black" :x 660 :y 375
        :connections [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [3 3 3 3 3 3 3 3] :development -1 :used false}
   :Bhowmik {:name "Bhowmik" :colour "Black" :x 560 :y 645
         :connections [:Walden :Ryss :Salman :Dengras :Glushko] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 2 3 3 4 5 4 3] :development -1 :used false}
   :Dengras {:name "Dengras" :colour "Black" :x 690 :y 580
         :connections [:Glushko :Bhowmik :Salman :Jaid :VanVogt :Marishka :Entli] :ships 0 :moved 0 :ship-colour "Black" 
             :production [5 3 1 2 3 3 3 3] :development -1 :used false}
   :Glushko {:name "Glushko" :colour "Black" :x 670 :y 700
         :connections [:Walden :Bhowmik :Dengras :Marishka] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 3 1 3 1 3 1 3] :development -1 :used false}
   :Marishka {:name "Marishka" :colour "Black" :x 800 :y 665
          :connections [:Glushko :Dengras :Entli] :ships 0 :moved 0 :ship-colour "Black"
              :production [0 0 0 1 2 2 3 2] :development -1 :used false}
   :Entli {:name "Entli" :colour "Black" :x 880 :y 590
         :connections [:Marishka :Dengras :VanVogt :Erasmus] :ships 0 :moved 0 :ship-colour "Black"
            :production [4 2 3 3 2 1 3 2] :development -1 :used false}
   :Erasmus {:name "Erasmus" :colour "Black" :x 985 :y 380
         :connections [:VanVogt :Iago :Nussbaum :Entli] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 3 2 4 3 5 4 2] :development -1 :used false}
   :Iago {:name "Iago" :colour "Black" :x 850 :y 340
        :connections [:Erasmus :VanVogt :Jaid :Lisst :Nussbaum] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 2 0 3 0 4 0 1] :development -1 :used false}
   :Lisst {:name "Lisst" :colour "Black" :x 780 :y 245
         :connections [:Iago :Jaid :Fignon :Nussbaum :Path :Byrd] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 1 1 1 1 2 2] :development -1 :used false}
   :Nussbaum {:name "Nussbaum" :colour "Black" :x 970 :y 250
          :connections [:Erasmus :Iago :Lisst :Byrd] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 4 5 3 4 5 5] :development -1 :used false}
   :Path {:name "Path" :colour "Black" :x 640 :y 160
        :connections [:Fignon :Xosa :Caia :Quinz :Byrd :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 0 1 2 3 5 6 6] :development -1 :used false}
   :Quinz {:name "Quinz" :colour "Black" :x 620 :y 30
         :connections [:Path :Caia :Byrd :Chiu] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 3 4 3 3 4 3 3] :development -1 :used false}
   :Byrd {:name "Byrd" :colour "Black" :x 800 :y 105
        :connections [:Lisst :Path :Quinz :Nussbaum] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 3 2 2 2] :development -1 :used false}
   }
   3
   {:Shoran {:name "Shoran" :colour "Black" :x 160 :y 180
        :connections [:Echemmon :Odyssey :Chiu] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 3 4 4 4 4 3] :development -1 :used false}
   :Echemmon {:name "Echemmon" :colour "Black" :x 290 :y 185
          :connections [:Odyssey :Shoran :Chiu :Xosa :Caia :Altu] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 3 4 4 5 6 5] :development -1 :used false}
   :Odyssey {:name "Odyssey" :colour "Black" :x 205 :y 290
         :connections [:Shoran :Echemmon :Altu :Uchino :Thalia] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 0 1 3 5 4 3 3] :development -1 :used false}
   :Chiu {:name "Chiu" :colour "Black" :x 345 :y 40
        :connections [:Caia :Shoran :Echemmon] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 2 2 3 3 3 3 3] :development -1 :used false}
   :Caia {:name "Caia" :colour "Black" :x 500 :y 85
        :connections [:Chiu :Xosa :Path :Echemmon] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 1 1 1 1 1 1 1] :development -1 :used false}
   :Xosa {:name "Xosa" :colour "Black" :x 485 :y 220
        :connections [:Echemmon :Caia :Algoa :Fignon :Path :Altu] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 4 3 2 1] :development -1 :used false}
   :Altu {:name "Altu" :colour "Black" :x 375 :y 300
        :connections [:Xosa :Odyssey :Echemmon :Algoa :Thalia] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 2 2 2 3 4] :development -1 :used false}
   :Uchino {:name "Uchino" :colour "Black" :x 165 :y 390
        :connections [:Odyssey :Thalia :Brahms] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 2 2 3 3 3 3] :development -1 :used false}
   :Thalia {:name "Thalia" :colour "Black" :x 320 :y 390
        :connections [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [5 4 4 3 3 3 2 2] :development -1 :used false}
   :Kanolta {:name "Kanolta" :colour "Black" :x 435 :y 460
         :connections [:Thalia :Algoa :Salman :Tyson :Ryss] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 3 4 4 4 4 4 5] :development -1 :used false}
   :Salman {:name "Salman" :colour "Black" :x 580 :y 510
        :connections [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 1 4 2 2 2] :development -1 :used false}
   :Brahms {:name "Brahms" :colour "Black" :x 195 :y 530
        :connections [:Uchino :Thalia :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 4 3 2 3 4 3 2] :development -1 :used false}
   :Tyson {:name "Tyson" :colour "Black" :x 290 :y 590
         :connections [:Brahms :Thalia :Kanolta :Ryss :Walden] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 0 0 0 6 7] :development -1 :used false}
   :Walden {:name "Walden" :colour "Black" :x 390 :y 710
        :connections [:Tyson :Ryss :Bhowmik] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 2 2 2 3 3 3] :development -1 :used false}
   :Ryss {:name "Ryss" :colour "Black" :x 425 :y 575
        :connections [:Walden :Tyson :Kanolta :Salman :Bhowmik] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 2 2] :development -1 :used false}
   :Fignon {:name "Fignon" :colour "Black" :x 600 :y 280
        :connections [:Algoa :Jaid :Xosa :Lisst :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 4 5 4] :development -1 :used false}
   :Algoa {:name "Algoa" :colour "Black" :x 510 :y 350
         :connections [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 5 6 7] :development -1 :used false}
    :VanVogt {:name "Van Vogt" :colour "Black" :x 785  :y 485
         :connections [:Jaid :Dengras :Iago] :ships 0 :moved 0 :ship-colour "Black"
             :production [3 3 3 4 4 5 5 6] :development -1 :used false}
   :Jaid {:name "Jaid" :colour "Black" :x 660 :y 375
        :connections [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [3 3 3 3 3 3 3 3] :development -1 :used false}
   :Bhowmik {:name "Bhowmik" :colour "Black" :x 560 :y 645
         :connections [:Walden :Ryss :Salman :Dengras] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 2 3 3 4 5 4 3] :development -1 :used false}
   :Dengras {:name "Dengras" :colour "Black" :x 690 :y 580
         :connections [:Bhowmik :Salman :Jaid :VanVogt] :ships 0 :moved 0 :ship-colour "Black" 
             :production [5 3 1 2 3 3 3 3] :development -1 :used false}
   :Iago {:name "Iago" :colour "Black" :x 850 :y 340
        :connections [:VanVogt :Jaid :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 2 0 3 0 4 0 1] :development -1 :used false}
   :Lisst {:name "Lisst" :colour "Black" :x 780 :y 245
         :connections [:Iago :Jaid :Fignon :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 1 1 1 1 2 2] :development -1 :used false}
   :Path {:name "Path" :colour "Black" :x 640 :y 160
        :connections [:Fignon :Xosa :Caia :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 0 1 2 3 5 6 6] :development -1 :used false}
   }
   2
   {:Echemmon {:name "Echemmon" :colour "Black" :x 290 :y 185
          :connections [:Odyssey :Xosa :Caia :Altu] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 3 4 4 5 6 5] :development -1 :used false}
   :Odyssey {:name "Odyssey" :colour "Black" :x 205 :y 290
         :connections [:Echemmon :Altu :Uchino :Thalia] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 0 1 3 5 4 3 3] :development -1 :used false}
   :Caia {:name "Caia" :colour "Black" :x 500 :y 85
        :connections [:Xosa :Path :Echemmon] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 1 1 1 1 1 1 1] :development -1 :used false}
   :Xosa {:name "Xosa" :colour "Black" :x 485 :y 220
        :connections [:Echemmon :Caia :Algoa :Fignon :Path :Altu] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 4 3 2 1] :development -1 :used false}
   :Altu {:name "Altu" :colour "Black" :x 375 :y 300
        :connections [:Xosa :Odyssey :Echemmon :Algoa :Thalia] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 2 2 2 3 4] :development -1 :used false}
   :Uchino {:name "Uchino" :colour "Black" :x 165 :y 390
        :connections [:Odyssey :Thalia :Brahms] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 2 2 3 3 3 3] :development -1 :used false}
   :Thalia {:name "Thalia" :colour "Black" :x 320 :y 390
        :connections [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [5 4 4 3 3 3 2 2] :development -1 :used false}
   :Kanolta {:name "Kanolta" :colour "Black" :x 435 :y 460
         :connections [:Thalia :Algoa :Salman :Tyson :Ryss] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 3 4 4 4 4 4 5] :development -1 :used false}
   :Salman {:name "Salman" :colour "Black" :x 580 :y 510
        :connections [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 1 4 2 2 2] :development -1 :used false}
   :Brahms {:name "Brahms" :colour "Black" :x 195 :y 530
        :connections [:Uchino :Thalia :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 4 3 2 3 4 3 2] :development -1 :used false}
   :Tyson {:name "Tyson" :colour "Black" :x 290 :y 590
         :connections [:Brahms :Thalia :Kanolta :Ryss] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 0 0 0 6 7] :development -1 :used false}
   :Ryss {:name "Ryss" :colour "Black" :x 425 :y 575
        :connections [:Tyson :Kanolta :Salman :Bhowmik] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 2 2] :development -1 :used false}
   :Fignon {:name "Fignon" :colour "Black" :x 600 :y 280
        :connections [:Algoa :Jaid :Xosa :Lisst :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 4 5 4] :development -1 :used false}
   :Algoa {:name "Algoa" :colour "Black" :x 510 :y 350
         :connections [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 5 6 7] :development -1 :used false}
    :VanVogt {:name "Van Vogt" :colour "Black" :x 785  :y 485
         :connections [:Jaid :Dengras :Iago] :ships 0 :moved 0 :ship-colour "Black"
             :production [3 3 3 4 4 5 5 6] :development -1 :used false}
   :Jaid {:name "Jaid" :colour "Black" :x 660 :y 375
        :connections [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [3 3 3 3 3 3 3 3] :development -1 :used false}
   :Bhowmik {:name "Bhowmik" :colour "Black" :x 560 :y 645
         :connections [:Ryss :Salman :Dengras] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 2 3 3 4 5 4 3] :development -1 :used false}
   :Dengras {:name "Dengras" :colour "Black" :x 690 :y 580
         :connections [:Bhowmik :Salman :Jaid :VanVogt] :ships 0 :moved 0 :ship-colour "Black" 
             :production [5 3 1 2 3 3 3 3] :development -1 :used false}
   :Iago {:name "Iago" :colour "Black" :x 850 :y 340
        :connections [:VanVogt :Jaid :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 2 0 3 0 4 0 1] :development -1 :used false}
   :Lisst {:name "Lisst" :colour "Black" :x 780 :y 245
         :connections [:Iago :Jaid :Fignon :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 1 1 1 1 2 2] :development -1 :used false}
   :Path {:name "Path" :colour "Black" :x 640 :y 160
        :connections [:Fignon :Xosa :Caia :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 0 1 2 3 5 6 6] :development -1 :used false}}})