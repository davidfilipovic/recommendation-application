(ns recommendation-application.views.index-page)

(defn- home-page2 []
  (layout/common 
        [:h1 (str "Hello " (session/flash-get :username))]
        [:h1 (str "Hellssso " (session/flash-get :name))]
        [:h1 (link-to "/logout" "Logout")]
        ;(get-ratings "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1")
       ;(first @get-link-for-every-game)
      ; (get-ratings "http://store.steampowered.com/app/730/?snr=1_7_7_230_150_1")
      [:br]
       [:br]
      ;(first (reviews "http://store.steampowered.com/app/440/?snr=1_7_7_230_150_1"))
     ;;(json/pprint (get-link-for-picture "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1"))
     ;(json/pprint (get-logo "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1"))
     ;(json/pprint (hickory-parser "http://store.steampowered.com/app/570/?snr=1_7_7_230_150_1" "screenshot_holder"))
     ;(json/pprint (get-game-score "http://store.steampowered.com/app/440/?snr=1_7_7_230_150_1"))
   ; (json/pprint (get-about-game "http://store.steampowered.com/app/440/?snr=1_7_7_230_150_1"))
   ;(get-game "http://store.steampowered.com/app/440/?snr=1_7_7_230_150_1")
  ; (get-game "http://store.steampowered.com/app/230230/?snr=1_7_7_230_150_1")
))