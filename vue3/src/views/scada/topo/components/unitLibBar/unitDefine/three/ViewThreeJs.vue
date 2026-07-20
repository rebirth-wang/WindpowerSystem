<template>
  <div class="webgl-container">
    <div id="webglDom" ref="webglDom"></div>
    <div :style="clickStyle" v-if="isShow" class="clickClass">
      <el-row style="font-size: 18px; margin-bottom: 30px">
        <el-col :span="8">
          <div>{{ panelName }}</div>
        </el-col>
        <el-col :span="8">
          <div>{{ $t('topo.three.028394-0') }}</div>
        </el-col>
        <el-col :span="8">
          <div>{{ $t('topo.three.028394-1') }}</div>
        </el-col>
      </el-row>
      <el-row style="font-size: 30px; color: aqua; margin-left: 30px">
        <el-col :span="8">
          <el-image
            style="width: 80px; height: 80px"
            src="http://gaohuacloud.com/prod-api/profile/avatar/2022/05/25/5a05b2bd-cbd3-46fe-b065-e1b7e0b0690c.png"
          ></el-image>
        </el-col>
        <el-col :span="12">
          <div style="margin-top: 15px">40888t</div>
        </el-col>
      </el-row>
      <el-row style="font-size: 18px">
        <el-col :span="12">
          <div>{{ $t('topo.three.028394-2') }}</div>
        </el-col>
        <el-col :span="12">
          <div>{{ $t('topo.three.028394-3') }}</div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts">
// 引入threejs核心模块
import { color } from 'echarts';
import * as THREE from 'three';
// 引入OrbitControls
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
// import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader";
// //长方体 参数：长，宽，高
// var geometry = new THREE.BoxGeometry(100, 100, 100);
// // 球体 参数：半径60  经纬度细分数40,40
// var geometry = new THREE.SphereGeometry(60, 40, 40);
// // 圆柱  参数：圆柱面顶部、底部直径50,50   高度100  圆周分段数
// var geometry = new THREE.CylinderGeometry( 50, 50, 100, 25 );
// // 正八面体
// var geometry = new THREE.OctahedronGeometry(50);
// // 正十二面体
// var geometry = new THREE.DodecahedronGeometry(50);
// // 正二十面体
// var geometry = new THREE.IcosahedronGeometry(50);
var scene = null;
var camera = null;
var renderer = null;
export default {
  name: 'ThreePage',
  data() {
    return {
      // scene: null,
      // camera: null,
      // renderer: null,
      textloader: null,
      width: 0,
      height: 0,
      clickStyle: {
        top: '10px',
        left: '50px',
      },
      isShow: false,
      panelName: '',
      controls: null,
      timer: null,
      num: 1,
    };
  },
  watch: {},
  beforeUnmount() {},
  mounted() {
    this.$nextTick(() => { this.init(); });
    window.addEventListener('click', this.onMouseClick, false);
  },
  methods: {
    onMouseClick(event) {
      var raycaster = new THREE.Raycaster();
      var mouse = new THREE.Vector2();
      //通过鼠标点击的位置计算出raycaster所需要的点的位置，以屏幕中心为原点，值的范围为-1到1.
      //通过鼠标点击位置，计算出raycaster所需点的位置，以屏幕为中心点，范围-1到1
      mouse.x = (event.clientX / renderer.domElement.clientWidth) * 2 - 1;
      mouse.y = -((event.clientY / renderer.domElement.clientHeight) * 2) + 1;
      // 通过鼠标点的位置和当前相机的矩阵计算出raycaster
      raycaster.setFromCamera(mouse, camera);

      // 获取raycaster直线和所有模型相交的数组集合
      console.log('666', scene.children);
      let clickMesh = [];
      scene.children.forEach((element) => {
        if (element.name.indexOf(this.$t('topo.three.028394-4')) > -1) {
          element.material.forEach((ele) => {
            ele.color.set('#ffffff');
          });
          clickMesh.push(element);
        }
      });
      console.log('clickMesh', clickMesh);
      var intersects = raycaster.intersectObjects(clickMesh);
      let meshs = [];
      console.log('intersects', intersects);
      //将所有的相交的模型的颜色设置为红色，如果只需要将第一个触发事件，那就数组的第一个模型改变颜色即可
      if (intersects.length > 0) {
        console.log('模型实体', intersects[0].object);
        console.log('鼠标点击', event);
        if (intersects[0].object.name) {
          this.panelName = intersects[0].object.name;
          console.log('meshs', meshs);
          intersects[0].object.material.forEach((ele) => {
            ele.color.set('#409EFF');
          });
          this.isShow = true;
          this.clickStyle = {
            top: event.clientY + 'px',
            left: event.clientX + 'px',
          };
        } else {
          this.isShow = false;
        }
      } else {
        this.isShow = false;
      }
    },
    init() {
      // 初始化画布宽高
      const container = this.$refs.webglDom;
      this.width = container.offsetWidth;
      this.height = container.offsetHeight;
      // 场景
      scene = new THREE.Scene();
      scene.fog = new THREE.Fog(0x005577, 1, 2800);
      this.textloader = new THREE.TextureLoader();

      // 相机
      camera = new THREE.PerspectiveCamera(90, this.width / this.height, 0.01, 10000);
      camera.position.set(1200, 320, 1220);
      this.add(camera);
      camera.lookAt(scene.position);

      // 坐标系
      var ip = window.location.host;
      if (ip.split(':')[0] == 'localhost' || ip.split(':')[0] == '192.168.0.111') {
        let axisHelper = new THREE.AxesHelper(1000);
        this.add(axisHelper);
      }
      // 添加灯光
      this.addLight();
      this.addBox(1200, 50, 0);
      this.addCyImage(300, 200, -600, this.$t('topo.three.028394-5'));
      this.addCyImage(650, 200, -600, this.$t('topo.three.028394-6'));
      this.addCyImage(950, 200, -600, this.$t('topo.three.028394-7'));
      this.addCyImage(300, 200, -300, this.$t('topo.three.028394-8'));
      this.addCyImage(650, 200, -300, this.$t('topo.three.028394-9'));
      this.addCyImage(950, 200, -300, this.$t('topo.three.028394-10'));
      this.addCyImage(300, 100, 0, this.$t('topo.three.028394-11'));
      this.addCyImage(650, 100, 0, this.$t('topo.three.028394-12'));
      this.addCyImage(950, 100, 0, this.$t('topo.three.028394-13'));
      this.addCyImage(300, 100, 350, this.$t('topo.three.028394-14'));
      this.addCyImage(650, 100, 350, this.$t('topo.three.028394-15'));
      this.addCyImage(950, 100, 350, this.$t('topo.three.028394-16'));
      this.addCyImage(300, 100, 600, this.$t('topo.three.028394-17'));
      this.addCyImage(650, 100, 600, this.$t('topo.three.028394-18'));
      this.addCyImage(950, 100, 600, this.$t('topo.three.028394-19'));
      this.addFbx();
      //this.addLeftPanel(50,40,0);
      //添加地板
      this.addPanel(600, 0, 0);
      this.addRoadPanel(-300, 0, -300);
      // 初始化一个加载器
      // 渲染器
      renderer = new THREE.WebGLRenderer({
        antialias: true,
      });
      renderer.setClearColor(new THREE.Color('#000000'), 1);
      renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
      renderer.setSize(this.width, this.height);
      document.getElementById('webglDom').appendChild(renderer.domElement);

      // OrbitControls
      new OrbitControls(camera, renderer.domElement);
      this.render();
    },
    addFbx() {
      var loader = new FBXLoader(); //创建一个FBX加载器
      loader.load(
        'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-bd83e627-0227-46f3-aef3-95bef99ce553/b26bcab2-c079-4b2a-9b8b-168b01aeff30.fbx',
        function (obj) {
          // console.log(obj);//查看加载后返回的模型对象
          scene.add(obj);
          // 适当平移fbx模型位置
          obj.position.set(1200, 0, 500);
        }
      );
    },
    //添加矩形
    addBox(x, y, z) {
      //创建一个长宽高的盒子模型
      const geometry = new THREE.BoxBufferGeometry(100, 100, 800);
      var materialsbg = [];
      let loader = new THREE.TextureLoader();
      for (var i = 0; i < 7; i++) {
        var material = new THREE.MeshBasicMaterial({
          map: loader.load('statics/topo/office/' + (i + 1) + '.png'),
          side: THREE.FrontSide,
        });
        materialsbg[i] = material;
      }
      const mesh = new THREE.Mesh(geometry, materialsbg);
      //x、z、y
      mesh.position.set(x, y, z);
      mesh.name = '仓库';
      scene.add(mesh);
    },
    addPanel(x, y, z) {
      // 前面FrontSide  背面：BackSide 双面：DoubleSide
      let loader = new THREE.TextureLoader();
      let texture = loader.load('statics/topo/office/floor3.png');
      let geometry = new THREE.PlaneGeometry(1500, 1500, 32);
      let material = new THREE.MeshBasicMaterial({
        map: texture, // 使用纹理贴图
        color: '#2a525d',
        side: THREE.DoubleSide, // 两面都渲染
      });
      // 设置阵列
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      // uv两个方向纹理重复数量
      texture.repeat.set(1, 1);
      let plane = new THREE.Mesh(geometry, material);
      plane.name = this.$t('topo.three.028394-20');
      plane.rotateX(Math.PI / 2);
      plane.position.set(x, y, z);
      scene.add(plane);
    },
    addRoadPanel(x, y, z) {
      // 前面FrontSide  背面：BackSide 双面：DoubleSide
      let loader = new THREE.TextureLoader();
      let texture = loader.load('statics/topo/office/road2.png');
      let geometry = new THREE.PlaneGeometry(100, 2500, 32);
      let material = new THREE.MeshBasicMaterial({
        map: texture, // 使用纹理贴图
        side: THREE.DoubleSide, // 两面都渲染
      });
      // 设置阵列
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      // uv两个方向纹理重复数量
      texture.repeat.set(1, 1);
      let plane = new THREE.Mesh(geometry, material);
      plane.rotateX(Math.PI / 2);
      plane.position.set(x, y, z);
      scene.add(plane);
    },
    //圆柱贴图
    addCyImage(x, z, y, name) {
      // 创建纹理加载器实例
      let cube;
      if (y > -300) {
        cube = new THREE.CylinderGeometry(60, 60, 200, 100, 1, false);
      } else {
        cube = new THREE.CylinderGeometry(60, 60, 400, 100, 1, false);
      }
      let cubeMaterials = [
        //side
        new THREE.MeshBasicMaterial({
          map: new THREE.TextureLoader().load('statics/topo/office/d001.png'),
          side: THREE.FrontSide,
          color: '#ffffff',
        }),
        //top
        new THREE.MeshBasicMaterial({ side: THREE.DoubleSide, color: '#ffffff' }),
        //bottom
        new THREE.MeshBasicMaterial({ side: THREE.DoubleSide, color: '#ffffff' }),
      ];
      const mesh = new THREE.Mesh(cube, cubeMaterials);
      mesh.rotateY(-Math.PI / 2 - 0.2);
      mesh.position.set(x, z, y);
      mesh.name = name;
      scene.add(mesh);
    },
    //添加后边界
    addAfterPanel() {
      let geometry = new THREE.PlaneGeometry(1500, 500);
      var ImageLoader = new THREE.ImageLoader();
      // load方法回调函数，按照路径加载图片，返回一个html的元素img对象
      let that = this;
      ImageLoader.load('statics/topo/office/dev3.png', function (img) {
        // image对象作为参数，创建一个纹理对象Texture
        var texture = new THREE.Texture(img);
        // 下次使用纹理时触发更新
        texture.needsUpdate = true;
        var material = new THREE.MeshLambertMaterial({
          map: texture, //设置纹理贴图
        });
        var mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
        // mesh.rotateY(Math.PI / 2)
        var group = new THREE.Group();
        group.position.set(0, 100, -750);
        group.add(mesh);
        that.add(group);
      });
    },
    //添加前边界
    addFrontPanel() {
      let geometry = new THREE.PlaneGeometry(1500, 500);
      var ImageLoader = new THREE.ImageLoader();
      // load方法回调函数，按照路径加载图片，返回一个html的元素img对象
      let that = this;
      ImageLoader.load('statics/topo/office/dev3.png', function (img) {
        // image对象作为参数，创建一个纹理对象Texture
        var texture = new THREE.Texture(img);
        // 下次使用纹理时触发更新
        texture.needsUpdate = true;
        var material = new THREE.MeshLambertMaterial({
          map: texture, //设置纹理贴图
        });
        var mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
        // mesh.rotateY(Math.PI / 2)
        var group = new THREE.Group();
        group.position.set(0, 100, 750);
        group.add(mesh);
        that.add(group);
      });
    },
    //添加左边界
    addLeftPanel(x, y, z) {
      // 前面FrontSide  背面：BackSide 双面：DoubleSide
      let loader = new THREE.TextureLoader();
      let texture = loader.load('statics/topo/office/road2.png');
      let geometry = new THREE.PlaneGeometry(1500, 100, 102);
      let material = new THREE.MeshBasicMaterial({
        map: texture, // 使用纹理贴图
        side: THREE.DoubleSide, // 两面都渲染
      });
      // 设置阵列
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      // uv两个方向纹理重复数量
      texture.repeat.set(1, 1);
      let plane = new THREE.Mesh(geometry, material);
      plane.rotateY(Math.PI / 2);
      plane.position.set(x, y, z);
      scene.add(plane);
    },
    //添加右边界
    addRightPanel() {
      let geometry = new THREE.PlaneGeometry(1500, 500);
      var ImageLoader = new THREE.ImageLoader();
      // load方法回调函数，按照路径加载图片，返回一个html的元素img对象
      let that = this;
      ImageLoader.load('statics/topo/office/dev3.png', function (img) {
        // image对象作为参数，创建一个纹理对象Texture
        var texture = new THREE.Texture(img);
        // 下次使用纹理时触发更新
        texture.needsUpdate = true;
        var material = new THREE.MeshLambertMaterial({
          map: texture, //设置纹理贴图
        });
        var mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
        mesh.rotateY(Math.PI / 2);
        var group = new THREE.Group();
        group.position.set(750, 100, 0);
        group.add(mesh);
        that.add(group);
      });
    },
    //添加球形
    addSphere(x, y, z) {
      //创建一个长宽高的盒子模型
      const geometry = new THREE.SphereGeometry(x, y, z);
      const material = new THREE.MeshPhongMaterial({
        color: 0xff0000,
      });
      const mesh = new THREE.Mesh(geometry, material);
      console.log('mesh', mesh);
      mesh.position.set(150, 0, 0);
      var group = new THREE.Group();
      group.position.set(150, 0, 0);
      group.add(mesh);
      this.add(group);
      console.log('本地坐标', mesh.position);
      var worldPosition = new THREE.Vector3();
      mesh.getWorldPosition(worldPosition);
      console.log('世界坐标', worldPosition);
    },
    //添加圆柱
    addCylinder() {
      const geometry = new THREE.CylinderGeometry(100, 100, 500, 100, 1, false);
      const material = new THREE.MeshPhongMaterial({
        color: '#ffffff',
      });
      const mesh = new THREE.Mesh(geometry, material);
      console.log('mesh', mesh);
      mesh.position.set(0, 0, 0);
      var group = new THREE.Group();
      group.position.set(0, 0, 0);
      group.add(mesh);
      this.add(group);
      console.log('本地坐标', mesh.position);
      var worldPosition = new THREE.Vector3();
      mesh.getWorldPosition(worldPosition);
      console.log('世界坐标', worldPosition);
    },
    addLight() {
      // 环境光
      const light = new THREE.AmbientLight(0xffffff, 0.5); // soft white light
      this.add(light);

      // // 平行光源
      const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
      directionalLight.position.set(200, 600, 200);
      this.add(directionalLight);
    },
    add(obj) {
      scene.add(obj);
    },
    render() {
      renderer.render(scene, camera);
      requestAnimationFrame(this.render);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#webglDom,
.webgl-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  padding: 2px;
}

.clickClass {
  position: fixed;
  color: aliceblue;
  padding: 40px;
  width: 400px;
  height: 250px;
  /* background-color: #7f7f7f; */
  background-image: url('http://gaohuacloud.com/prod-api/profile/avatar/2022/07/22/panelThree_20220722104950A023.png');
  background-size: cover;
}
</style>
