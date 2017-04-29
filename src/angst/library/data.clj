(ns angst.library.data)

(def all-empires
  {:Sheep {:name "Sheep" :colour "Blue" :resources 8 :vp 0 :major ""}
     :Gopher {:name "Gopher" :colour "Green" :resources 8 :vp 0 :major ""}
     :Muskox {:name "Muskox" :colour "Red" :resources 8 :vp 0 :major ""}
     :Llama {:name "Llama" :colour "Yellow" :resources 8 :vp 0 :major ""}
     :Flamingo {:name "Flamingo" :colour "Pink" :resources 8 :vp 0 :major ""}})

(def button-map
  {:setup-new-game {:label "Start new game!" :x 683 :y 650 :width 300 :height 50 :effect [[:new-game]]}
  :setup-load {:label "Load from save" :x 683 :y 580 :width 300 :height 50 :effect [[:load]]}
  :choose-sheep {:label "Sheep Empire: No" :x 483 :y 250 :width 150 :height 40 :effect [[:toggle-empire :choose-sheep :Sheep]]}
  :choose-gopher {:label "Gopher Empire: No" :x 483 :y 300 :width 150 :height 40 :effect [[:toggle-empire :choose-gopher :Gopher]]}
  :choose-muskox {:label "Muskox Empire: No" :x 483 :y 350 :width 150 :height 40 :effect [[:toggle-empire :choose-muskox :Muskox]]}
  :choose-llama {:label "Llama Empire: No" :x 483 :y 400 :width 150 :height 40 :effect [[:toggle-empire :choose-llama :Llama]]}
  :choose-flamingo {:label "Flamingo Empire: No" :x 483 :y 450 :width 150 :height 40 :effect [[:toggle-empire :choose-flamingo :Flamingo]]}
  :opt-rand-start {:label "Random Start: Off" :x 883 :y 250 :width 150 :height 40 :effect [[:toggle-option :opt-rand-start "rand-start"]]}
  :opt-objectives {:label "Objectives: Off" :x 883 :y 300 :width 150 :height 40 :effect [[:toggle-option :opt-objectives "goals"]]}
  :game-save {:label "Save" :x 900 :y 40 :width 80 :height 40 :effect [[:save]]}
  :game-load {:label "Load" :x 800 :y 40 :width 80 :height 40 :effect [[:load]]}
  :game-quit {:label "Quit" :x 1000 :y 40 :width 80 :height 40 :effect [[:menu]]}
  :end-phase {:label "End Specialization Phase" :x 1216 :y 344 :width 200 :height 50 :effect [[:end-phase]]}
  :cancel-move {:label "Cancel" :x 1216 :y 344 :width 200 :height 50 :effect [[:cancel-move]]}
  :done-command {:label "Done Command" :x 1216 :y 344 :width 200 :height 50 :effect [[:done-command]]}
  :cancel-ability {:label "Done"  :x 1216 :y 344 :width 200 :height 50 :effect [[:cancel-ability]]}})

(def phase-labels
  ["End Specialization Phase" "End Production Phase" "End Command Phase" "End Construction Phase" "Done Colonizing"])

(def setup-state
  {:phase "setup"
   :options #{}
   :empires #{}
   :buttons (select-keys button-map [:setup-new-game :setup-load :choose-sheep :choose-flamingo :choose-llama
                                     :choose-muskox :choose-gopher :opt-objectives :opt-rand-start])})

(def init-state
  {:planets {}
   :display {:planet false
             :infobar-width 300}
   :empire {}
   :active :Sheep
   :phase 0 ;0: Specialization, 1 Production, 2 Command, 3 Construction, 4 colonization
   ; Buttons coordinates given for center of button
   :buttons (select-keys button-map [:end-phase :game-save :game-load :game-quit])
   :active-planet false ; Either false or planet keyword
   :next-player-map {}
   :constant-effects {:Sheep [] :Gopher [] :Muskox [] :Llama [] :Flamingo [] :phase-end [] :turn-end [] :projects []}
   :effect-details {}
   :effects {:ship-move false}
   :prompt-stack []
   })

(def all-planets
  {:Petiska {:name "Petiska" :colour "Black" :x 85 :y 48
         :connections [:Henz :Shoran :Kazo] :ships 0 :moved 0 :ship-colour "Black"
         :production [1 1 2 2 3 3 3 4] :development -1 :used false :project false
        :ability "Move any of your fleets, paying movement costs."}

   :Henz {:name "Henz" :colour "Black" :x 195 :y 45
        :connections [:Echemmon :Chiu :Petiska  :Shoran] :ships 0 :moved 0 :ship-colour "Black"
        :production [4 4 3 3 2 2 1 1] :development -1 :used false :project false
        :ability "2\u0398: Gain 1 VP"}

   :Shoran {:name "Shoran" :colour "Black" :x 160 :y 180
        :connections [:Echemmon :Odyssey :Petiska :Henz :Kazo] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 3 4 4 4 4 3] :development -1 :used false :project false
        :ability "Gain 2\u0398 for each ship you send into combat this turn."}

    :Kazo {:name "Kazo" :colour "Black" :x 40 :y 200
        :connections [:Odyssey :Petiska :Shoran :Valeria] :ships 0 :moved 0 :ship-colour "Black"
           :production [2 2 2 3 3 3 3 3] :development -1 :used false :project false
        :ability "Choose a planet and pay 1\u0398 per ship on that planet. Ships cannot be moved off that planet until your next turn."}

   :Echemmon {:name "Echemmon" :colour "Black" :x 290 :y 185
          :connections [:Odyssey :Henz :Shoran :Chiu :Xosa :Caia :Altu] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 3 4 4 5 6 5] :development -1 :used false :project false
        :ability "Set a planet to 'unused'. You may not use its special ability for the rest of this turn."}

   :Odyssey {:name "Odyssey" :colour "Black" :x 205 :y 290
         :connections [:Valeria :Kazo :Shoran :Echemmon :Altu :Uchino :Thalia] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 0 1 3 5 4 3 3] :development -1 :used false :project false
        :ability "10\u0398: Gain 16\u0398."}

   :Chiu {:name "Chiu" :colour "Black" :x 345 :y 40
        :connections [:Caia :Henz :Echemmon :Quinz] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 2 2 3 3 3 3 3] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: All your projects start with 1 extra \u00A7 for every 2\u00A7 on Chiu."}

   :Caia {:name "Caia" :colour "Black" :x 500 :y 85
        :connections [:Chiu :Xosa :Path :Quinz :Echemmon] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 1 1 1 1 1 1 1] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: Spend 7 \u00A7 to destroy a planet. During the Specialization phase, you may  spend \u0398 equal to the amountof \u00A7 on Caia  to add 1\u00A7."}

   :Xosa {:name "Xosa" :colour "Black" :x 485 :y 220
        :connections [:Echemmon :Caia :Algoa :Fignon :Path :Altu] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 4 3 2 1] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: Spend 3 \u00A7 to remove 2 ships from any planet."}

   :Altu {:name "Altu" :colour "Black" :x 375 :y 300
        :connections [:Xosa :Odyssey :Echemmon :Algoa :Thalia] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 2 2 2 3 4] :development -1 :used false :project false
        :ability "Gain 2\u0398 when you use a planet's special ability or start a project."}

   :Valeria {:name "Valeria" :colour "Black" :x 35 :y 350
         :connections [:Kazo :Odyssey :Uchino :Zellner] :ships 0 :moved 0 :ship-colour "Black"
             :production [0 1 2 3 4 5 5 5] :development -1 :used false :project false
        :ability "Remove one of your ships. Gain 6\u0398."}

   :Uchino {:name "Uchino" :colour "Black" :x 165 :y 390
        :connections [:Valeria :Odyssey :Thalia :Zellner :Brahms] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 2 2 3 3 3 3] :development -1 :used false :project false
        :ability "3\u0398: Build a ship on a connected planet."}

   :Thalia {:name "Thalia" :colour "Black" :x 320 :y 390
        :connections [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [5 4 4 3 3 3 2 2] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: All your planets have +1 defense for every 3\u00A7."}

   :Kanolta {:name "Kanolta" :colour "Black" :x 435 :y 460
         :connections [:Thalia :Algoa :Salman :Tyson :Ryss] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 3 4 4 4 4 4 5] :development -1 :used false :project false
        :ability "Move any number of your ships here."}

   :Salman {:name "Salman" :colour "Black" :x 580 :y 510
        :connections [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 1 4 2 2 2] :development -1 :used false :project false
        :ability "1\u0398: Build 1 ship."}

   :Zellner {:name "Zellner" :colour "Black" :x 40 :y 530
         :connections [:Valeria :Uchino :Brahms :Tomaso] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 2 2 2 2 2 2 5] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: All your planets less than \u00A7 spaces away have +1 defense."}

   :Brahms {:name "Brahms" :colour "Black" :x 195 :y 530
        :connections [:Zellner :Uchino :Thalia :Tomaso :Tyson] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 4 3 2 3 4 3 2] :development -1 :used false :project "inactive" :progress 0
        :ability "Increase all travel costs by 2 until your next turn."}

   :Tomaso {:name "Tomaso" :colour "Black" :x 115 :y 640
        :connections [:Zellner :Brahms :Tyson :Walden] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 3 4 3 3 3 3] :development -1 :used false :project false
        :ability "Reduce all travel costs by 2 this turn."}

   :Tyson {:name "Tyson" :colour "Black" :x 290 :y 590
         :connections [:Tomaso :Brahms :Thalia :Kanolta :Ryss :Walden] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 0 0 0 0 0 6 7] :development -1 :used false :project false
        :ability "Move a ship from Tyson to any planet that is not controlled by another player."}

   :Walden {:name "Walden" :colour "Black" :x 390 :y 710
        :connections [:Tomaso :Tyson :Ryss :Bhowmik :Glushko] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 2 2 2 2 3 3 3] :development -1 :used false :project false
        :ability "Increase all your planets' development by 1."}

   :Ryss {:name "Ryss" :colour "Black" :x 425 :y 575
        :connections [:Walden :Tyson :Kanolta :Salman :Bhowmik] :ships 0 :moved 0 :ship-colour "Black"
           :production [4 4 3 3 2 2 2 2] :development -1 :used false :project false
        :ability "2\u0398: Choose a planet. That planet may not be used until your next turn."}

   :Fignon {:name "Fignon" :colour "Black" :x 600 :y 280
        :connections [:Algoa :Jaid :Xosa :Lisst :Path] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 4 5 4] :development -1 :used false :project false
        :ability "Gain 3\u0398."}

   :Algoa {:name "Algoa" :colour "Black" :x 510 :y 350
         :connections [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman] :ships 0 :moved 0 :ship-colour "Black"
            :production [0 1 2 3 4 5 6 7] :development -1 :used false :project false
        :ability "8\u0398: Build 3 ships here."}

    :VanVogt {:name "Van Vogt" :colour "Black" :x 785  :y 485
         :connections [:Jaid :Dengras :Entli :Erasmus :Iago :Froya] :ships 0 :moved 0 :ship-colour "Black"
             :production [3 3 3 4 4 5 5 6] :development -1 :used false :project false
        :ability "2\u0398: Remove a ship from Van Vogt and colonize any uncolonized planet (paying  its colonization cost)."}

   :Jaid {:name "Jaid" :colour "Black" :x 660 :y 375
        :connections [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [3 3 3 3 3 3 3 3] :development -1 :used false :project false
        :ability "Whenever your planets gain \u00A7 until your next turn, they gain an additional \u00A7"}

   :Bhowmik {:name "Bhowmik" :colour "Black" :x 560 :y 645
         :connections [:Walden :Ryss :Salman :Dengras :Glushko] :ships 0 :moved 0 :ship-colour "Black"
             :production [2 2 3 3 4 5 4 3] :development -1 :used false :project "inactive" :progress 0
        :ability "Spend 1\u00A7: Gain 1\u0398 for each \u00A7 on Bhowmik, including the one removed."}

   :Dengras {:name "Dengras" :colour "Black" :x 690 :y 580
         :connections [:Glushko :Bhowmik :Salman :Jaid :VanVogt :Marishka :Entli] :ships 0 :moved 0 :ship-colour "Black" 
             :production [5 3 1 2 3 3 3 3] :development -1 :used false :project false
        :ability "Whenever a player attacks one of your planets, that player loses 4 resources."}

   :Glushko {:name "Glushko" :colour "Black" :x 670 :y 700
         :connections [:Walden :Bhowmik :Dengras :Marishka] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 3 1 3 1 3 1 3] :development -1 :used false :project false
        :ability "Choose a planet. The player that controls that planet loses 4\u0398."}

   :Marishka {:name "Marishka" :colour "Black" :x 800 :y 665
          :connections [:Glushko :Dengras :Entli :Beek] :ships 0 :moved 0 :ship-colour "Black"
              :production [0 0 0 1 2 2 3 2] :development -1 :used false :project false
        :ability "Whenever you conquer a planet this turn, place one ship there."}

   :Entli {:name "Entli" :colour "Black" :x 880 :y 590
         :connections [:Beek :Marishka :Dengras :VanVogt :Froya] :ships 0 :moved 0 :ship-colour "Black"
            :production [4 2 3 3 2 1 3 2] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: Spend 5\u00A7 to build 3 ships on any planet you control."}

   :Beek {:name "Beek" :colour "Black" :x 940 :y 685
        :connections [:Marishka :Entli :Froya] :ships 0 :moved 0 :ship-colour "Black"
           :production [1 2 3 4 4 3 2 1] :development -1 :used false :project false
        :ability "2\u0398: Use the non-project special ability of a planet you control."}

   :Froya {:name "Froya" :colour "Black" :x 1000 :y 550
         :connections [:Beek :Entli :Erasmus :VanVogt] :ships 0 :moved 0 :ship-colour "Black"
            :production [2 2 2 3 3 3 3 3] :development -1 :used false :project false
        :ability "6\u0398: All your planets have +1 defense until your next turn."}

   :Erasmus {:name "Erasmus" :colour "Black" :x 985 :y 380
         :connections [:Froya :VanVogt :Iago :Nussbaum] :ships 0 :moved 0 :ship-colour "Black"
             :production [1 3 2 4 3 5 4 2] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: Remove X\u00A7 to place X-1  on Erasmus."}

   :Iago {:name "Iago" :colour "Black" :x 850 :y 340
        :connections [:Erasmus :VanVogt :Jaid :Lisst :Nussbaum] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 2 0 3 0 4 0 1] :development -1 :used false :project false
        :ability "3\u0398: use a non-project special ability of an uncolonized planet connected to a planet you control."}

   :Lisst {:name "Lisst" :colour "Black" :x 780 :y 245
         :connections [:Iago :Jaid :Fignon :Nussbaum :Path :Byrd :Yerba] :ships 0 :moved 0 :ship-colour "Black"
            :production [1 1 1 1 1 1 2 2] :development -1 :used false :project false
        :ability "5\u0398: Build two ships on this planet."}

   :Nussbaum {:name "Nussbaum" :colour "Black" :x 970 :y 250
          :connections [:Erasmus :Iago :Lisst :Yerba] :ships 0 :moved 0 :ship-colour "Black"
              :production [2 3 4 5 3 4 5 5] :development -1 :used false :project false
        :ability "Add 1\u00A7 to an active project"}

   :Path {:name "Path" :colour "Black" :x 640 :y 160
        :connections [:Fignon :Xosa :Caia :Quinz :Byrd :Lisst] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 0 1 2 3 5 6 6] :development -1 :used false :project false
        :ability "Choose a planet. That planet has +2 defense until your next turn."}

   :Quinz {:name "Quinz" :colour "Black" :x 620 :y 30
         :connections [:Path :Caia :Byrd :Chiu] :ships 0 :moved 0 :ship-colour "Black"
            :production [3 3 4 3 3 4 3 3] :development -1 :used false :project false
        :ability "4\u0398: Choose a planet. That planet has -1 defense this turn."}

   :Byrd {:name "Byrd" :colour "Black" :x 800 :y 105
        :connections [:Lisst :Path :Quinz :Yerba] :ships 0 :moved 0 :ship-colour "Black"
           :production [0 1 2 3 3 2 2 2] :development -1 :used false :project "inactive" :progress 0
        :ability "Project: Reduce travel costs by 1 for each\u00A7."}

   :Yerba {:name "Yerba" :colour "Black" :x 925 :y 100
         :connections [:Nussbaum :Lisst :Byrd] :ships 0 :moved 0 :ship-colour "Black"
            :production [4 3 2 1 1 1 1 0] :development -1 :used false :project false
        :ability "Colonization requires no \u0398 this turn."}
   })

(def planet-maps

  ; Stores information for planets and connections based on # of players. 5-player game uses
  ;   all-planets unchanged (future fix for consistency?)

  {
   4
  {:Henz [:Echemmon :Chiu :Shoran]
   :Shoran[:Echemmon :Odyssey :Henz]
   :Echemmon [:Odyssey :Henz :Shoran :Chiu :Xosa :Caia :Altu]
   :Odyssey [:Shoran :Echemmon :Altu :Uchino :Thalia]
   :Chiu [:Caia :Henz :Echemmon :Quinz]
   :Caia [:Chiu :Xosa :Path :Quinz :Echemmon]
   :Xosa [:Echemmon :Caia :Algoa :Fignon :Path :Altu]
   :Altu [:Xosa :Odyssey :Echemmon :Algoa :Thalia]
   :Uchino [:Odyssey :Thalia :Brahms]
   :Thalia [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson]
   :Kanolta [:Thalia :Algoa :Salman :Tyson :Ryss]
   :Salman [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras]
   :Brahms [:Uchino :Thalia :Tyson]
   :Tyson [:Brahms :Thalia :Kanolta :Ryss :Walden]
   :Walden [:Tyson :Ryss :Bhowmik :Glushko]
   :Ryss [:Walden :Tyson :Kanolta :Salman :Bhowmik]
   :Fignon [:Algoa :Jaid :Xosa :Lisst :Path]
   :Algoa [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman]
   :VanVogt [:Jaid :Dengras :Entli :Erasmus :Iago]
   :Jaid [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst] 
   :Bhowmik [:Walden :Ryss :Salman :Dengras :Glushko]
   :Dengras [:Glushko :Bhowmik :Salman :Jaid :VanVogt :Marishka :Entli]
   :Glushko [:Walden :Bhowmik :Dengras :Marishka]
   :Marishka [:Glushko :Dengras :Entli]
   :Entli [:Marishka :Dengras :VanVogt :Erasmus]
   :Erasmus [:VanVogt :Iago :Nussbaum :Entli]
   :Iago [:Erasmus :VanVogt :Jaid :Lisst :Nussbaum]
   :Lisst [:Iago :Jaid :Fignon :Nussbaum :Path :Byrd]
   :Nussbaum [:Erasmus :Iago :Lisst :Byrd]
   :Path [:Fignon :Xosa :Caia :Quinz :Byrd :Lisst]
   :Quinz [:Path :Caia :Byrd :Chiu]
   :Byrd [:Lisst :Path :Quinz :Nussbaum]
   }

   3
   {:Shoran [:Echemmon :Odyssey :Chiu]
   :Echemmon [:Odyssey :Shoran :Chiu :Xosa :Caia :Altu]
   :Odyssey [:Shoran :Echemmon :Altu :Uchino :Thalia]
   :Chiu [:Caia :Shoran :Echemmon]
   :Caia [:Chiu :Xosa :Path :Echemmon]
   :Xosa [:Echemmon :Caia :Algoa :Fignon :Path :Altu]
   :Altu [:Xosa :Odyssey :Echemmon :Algoa :Thalia]
   :Uchino [:Odyssey :Thalia :Brahms]
   :Thalia [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson]
   :Kanolta [:Thalia :Algoa :Salman :Tyson :Ryss]
   :Salman [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras]
   :Brahms [:Uchino :Thalia :Tyson]
   :Tyson [:Brahms :Thalia :Kanolta :Ryss :Walden]
   :Walden [:Tyson :Ryss :Bhowmik]
   :Ryss [:Walden :Tyson :Kanolta :Salman :Bhowmik]
   :Fignon [:Algoa :Jaid :Xosa :Lisst :Path]
   :Algoa [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman]
   :VanVogt [:Jaid :Dengras :Iago]
   :Jaid [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst]
   :Bhowmik [:Walden :Ryss :Salman :Dengras]
   :Dengras [:Bhowmik :Salman :Jaid :VanVogt]
   :Iago [:VanVogt :Jaid :Lisst]
   :Lisst [:Iago :Jaid :Fignon :Path]
   :Path [:Fignon :Xosa :Caia :Lisst]

   }
   2
   {:Echemmon [:Odyssey :Xosa :Caia :Altu]
   :Odyssey [:Echemmon :Altu :Uchino :Thalia]
   :Caia [:Xosa :Path :Echemmon]
   :Xosa [:Echemmon :Caia :Algoa :Fignon :Path :Altu]
   :Altu [:Xosa :Odyssey :Echemmon :Algoa :Thalia]
   :Uchino [:Odyssey :Thalia :Brahms]
   :Thalia [:Uchino :Odyssey :Altu :Algoa :Kanolta :Brahms :Tyson]
   :Kanolta [:Thalia :Algoa :Salman :Tyson :Ryss]
   :Salman [:Kanolta :Algoa :Jaid :Ryss :Bhowmik :Dengras]
   :Brahms [:Uchino :Thalia :Tyson]
   :Tyson [:Brahms :Thalia :Kanolta :Ryss]
   :Ryss [:Tyson :Kanolta :Salman :Bhowmik]
   :Fignon [:Algoa :Jaid :Xosa :Lisst :Path]
   :Algoa [:Fignon :Jaid :Altu :Xosa :Thalia :Kanolta :Salman]
   :VanVogt [:Jaid :Dengras :Iago]
   :Jaid [:Fignon :Algoa :VanVogt :Salman :Dengras :Iago :Lisst]
   :Bhowmik [:Ryss :Salman :Dengras]
   :Dengras [:Bhowmik :Salman :Jaid :VanVogt]
   :Iago [:VanVogt :Jaid :Lisst]
   :Lisst [:Iago :Jaid :Fignon :Path]
   :Path [:Fignon :Xosa :Caia :Lisst]}})