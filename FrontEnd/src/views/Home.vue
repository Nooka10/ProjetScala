<template>
  <div>
    <p>dsd</p>
      <a-input-search
      placeholder="input search text"
      @search="onSearch"
      enterButton
    />
  </div>
</template>

<script>
import { Input } from 'ant-design-vue';

export default {
  components: {
    aInputSearch: Input,
  },
  methods: {
    onSearch (value) {
      console.log(value)
    },
  },
}

</script>