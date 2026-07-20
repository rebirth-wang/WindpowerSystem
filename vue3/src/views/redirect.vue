<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { onMounted } from 'vue';

const route = useRoute();
const router = useRouter();

onMounted(() => {
  const { params, query } = route;
  const { path } = params;
  router.replace({ path: '/' + path, query });
});
</script>
