package com.toonapps.toon.view.fragments;

/**
 * Interface used so the fragment can talk to the underlying activity
 * to start an action
 */
public interface OnFragmentInteractionListener {

    interface ACTION {
        interface START {
            int GAS_USAGE = 0;
            int ELEC_USAGE = 1;
        }
    }

    void onFragmentInteraction(int action);
}