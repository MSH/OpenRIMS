var webpack = require('webpack');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
var path = require('path');


module.exports = {
  mode: 'production',
  entry: {
    application: path.resolve(__dirname, './src/js/application.js')
  },   
  
  module: {
      rules: [        
        {
          test: /\.(js|jsx)$/,
          exclude: /node_modules/,
          use: {
            loader: "babel-loader",
            options: {
              presets: [['@babel/preset-react'], ['@babel/preset-env',{
                "debug": true,
              }]],
            }
          }
        }, 
        {
          test: /\.css$/,
          use: [
            'style-loader',
            'css-loader'
          ]
        },
        {
          test: /\.scss$/,
          use: [
            'style-loader',
            'css-loader',
            'sass-loader'
          ]
        },
        {
          test: /\.(ttf|eot|png|svg|jpg|gif)$/,
           use: [
            'file-loader'
          ]
        },
        { 
          test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, 
          loader: "url-loader",
          options:{limit:10000,mimetype:"application/font-woff"}  
        },       
      ]
  }, 
  plugins:[
    //new BundleAnalyzerPlugin(),
    new  CleanWebpackPlugin()
  ],

  output: {
    //path: path.resolve('C:/eclipse/workspace/pharmadex2/src/main/resources/static/', 'js'), 
    path: path.resolve('C:/eclipse/jee-2024-06/workspace/pharmadex2/src/main/resources/static', 'js'), 
    publicPath: '/js/',
    filename: '[name].[fullhash].js',
  },
};