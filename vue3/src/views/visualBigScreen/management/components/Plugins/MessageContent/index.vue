<!-- eslint-disable vue/valid-template-root -->
<template></template>

<script lang="ts" setup>
import { useMessage } from 'naive-ui'
import { setMessageApi } from '@vb/utils/runtimeContext'

setMessageApi(useMessage())
</script>
