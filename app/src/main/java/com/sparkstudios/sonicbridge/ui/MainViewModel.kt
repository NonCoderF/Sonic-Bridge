package com.sparkstudios.sonicbridge.ui

import androidx.lifecycle.ViewModel
import com.sparkstudios.sonicbridge.data.PlaybackStateRepository
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        PlaybackStateRepository.state

}