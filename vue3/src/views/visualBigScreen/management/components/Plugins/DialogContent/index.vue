<!-- eslint-disable vue/valid-template-root -->
<template></template>
<script lang="ts" setup>
import { useDialog } from 'naive-ui'
import { setDialogApi } from '@vb/utils/runtimeContext'

setDialogApi(useDialog())
</script>
