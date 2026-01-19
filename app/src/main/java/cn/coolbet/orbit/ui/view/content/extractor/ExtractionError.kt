package cn.coolbet.orbit.ui.view.content.extractor

sealed class ExtractionError : Exception() {
    object FailedToExtract : ExtractionError() {
        private fun readResolve(): Any = FailedToExtract
    }

    object DataIsNotString : ExtractionError() {
        private fun readResolve(): Any = DataIsNotString
    }

    object MissingExtractionData : ExtractionError() {
        private fun readResolve(): Any = MissingExtractionData
    }

    data class NetworkError(override val cause: Throwable) : ExtractionError()
}