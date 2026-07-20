<!-- eslint-disable vue/valid-template-root -->
<template></template>

<script lang="ts" setup>
import { useLoadingBar } from 'naive-ui'
import { setLoadingBarApi } from '@vb/utils/runtimeContext'

const loadingBar = useLoadingBar()
setLoadingBarApi(loadingBar)
loadingBar.finish()
</script>
