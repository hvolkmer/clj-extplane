(ns extplane.mappings)

;; Mappings from http://www.xsquawkbox.net/xpsdk/mediawiki/XPLMUtilities
(def keys_enum
[:key_pause,
:key_revthrust,
:key_jettison,
:key_brakesreg,
:key_brakesmax,
:key_gear,
:key_timedn,
:key_timeup,
:key_fadec,
:key_otto_dis,
:key_otto_atr,
:key_otto_asi,
:key_otto_hdg,
:key_otto_gps,
:key_otto_lev,
:key_otto_hnav,
:key_otto_alt,
:key_otto_vvi,
:key_otto_vnav,
:key_otto_nav1,
:key_otto_nav2,
:key_targ_dn,
:key_targ_up,
:key_hdgdn,
:key_hdgup,
:key_barodn,
:key_baroup,
:key_obs1dn,
:key_obs1up,
:key_obs2dn,
:key_obs2up,
:key_com1_1,
:key_com1_2,
:key_com1_3,
:key_com1_4,
:key_nav1_1,
:key_nav1_2,
:key_nav1_3,
:key_nav1_4,
:key_com2_1,
:key_com2_2,
:key_com2_3,
:key_com2_4,
:key_nav2_1,
:key_nav2_2,
:key_nav2_3,
:key_nav2_4,
:key_adf_1,
:key_adf_2,
:key_adf_3,
:key_adf_4,
:key_adf_5,
:key_adf_6,
:key_transpon_1,
:key_transpon_2,
:key_transpon_3,
:key_transpon_4,
:key_transpon_5,
:key_transpon_6,
:key_transpon_7,
:key_transpon_8,
:key_flapsup,
:key_flapsdn,
:key_cheatoff,
:key_cheaton,
:key_sbrkoff,
:key_sbrkon,
:key_ailtrimL,
:key_ailtrimR,
:key_rudtrimL,
:key_rudtrimR,
:key_elvtrimD,
:key_elvtrimU,
:key_forward,
:key_down,
:key_left,
:key_right,
:key_back,
:key_tower,
:key_runway,
:key_chase,
:key_free1,
:key_free2,
:key_spot,
:key_fullscrn1,
:key_fullscrn2,
:key_tanspan,
:key_smoke,
:key_map,
:key_zoomin,
:key_zoomout,
:key_cycledump,
:key_replay,
:key_tranID,
:key_max])

(def buttons_enum
[:joy_nothing,
:joy_start_all,
:joy_start_0,
:joy_start_1,
:joy_start_2,
:joy_start_3,
:joy_start_4,
:joy_start_5,
:joy_start_6,
:joy_start_7,
:joy_throt_up,
:joy_throt_dn,
:joy_prop_up,
:joy_prop_dn,
:joy_mixt_up,
:joy_mixt_dn,
:joy_carb_tog,
:joy_carb_on,
:joy_carb_off,
:joy_trev,
:joy_trm_up,
:joy_trm_dn,
:joy_rot_trm_up,
:joy_rot_trm_dn,
:joy_rud_lft,
:joy_rud_cntr,
:joy_rud_rgt,
:joy_ail_lft,
:joy_ail_cntr,
:joy_ail_rgt,
:joy_B_rud_lft,
:joy_B_rud_rgt,
:joy_look_up,
:joy_look_dn,
:joy_look_lft,
:joy_look_rgt,
:joy_glance_l,
:joy_glance_r,
:joy_v_fnh,
:joy_v_fwh,
:joy_v_tra,
:joy_v_twr,
:joy_v_run,
:joy_v_cha,
:joy_v_fr1,
:joy_v_fr2,
:joy_v_spo,
:joy_flapsup,
:joy_flapsdn,
:joy_vctswpfwd,
:joy_vctswpaft,
:joy_gear_tog,
:joy_gear_up,
:joy_gear_down,
:joy_lft_brake,
:joy_rgt_brake,
:joy_brakesREG,
:joy_brakesMAX,
:joy_speedbrake,
:joy_ott_dis,
:joy_ott_atr,
:joy_ott_asi,
:joy_ott_hdg,
:joy_ott_alt,
:joy_ott_vvi,
:joy_tim_start,
:joy_tim_reset,
:joy_ecam_up,
:joy_ecam_dn,
:joy_fadec,
:joy_yaw_damp,
:joy_art_stab,
:joy_chute,
:joy_JATO,
:joy_arrest,
:joy_jettison,
:joy_fuel_dump,
:joy_puffsmoke,
:joy_prerotate,
:joy_UL_prerot,
:joy_UL_collec,
:joy_TOGA,
:joy_shutdown,
:joy_con_atc,
:joy_fail_now,
:joy_pause,
:joy_rock_up,
:joy_rock_dn,
:joy_rock_lft,
:joy_rock_rgt,
:joy_rock_for,
:joy_rock_aft,
:joy_idle_hilo,
:joy_lanlights,
:joy_max])

(defn get-mapping [mappings name]
   ((into {} (keep-indexed (fn [p v] [v p]) mappings)) name))

(defn get-key-mapping [name]
  (get-mapping keys_enum name))
  
(defn get-button-mapping [name]
  (get-mapping buttons_enum name))
